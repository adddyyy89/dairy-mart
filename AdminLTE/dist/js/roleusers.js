const ROLE_PAGES = {
  2: { title: 'Salesmen', addHref: './adduser.html?type=2', empty: 'No salesmen found.', noun: 'salesman' },
  3: { title: 'Retailers', addHref: './adduser.html?type=3', empty: 'No retailers found.', noun: 'retailer' },
};

function typeLabel(user, fallbackId) {
  return user?.type?.userTypeDesc
    || user?.userTypeDesc
    || (Number(user?.userTypeId || user?.typeId) === 2 ? 'Salesman' : Number(fallbackId) === 2 ? 'Salesman' : 'Retailer');
}

function isActiveUser(user) {
  return user.active !== false && user.isActive !== false;
}

async function setShopsActiveForUser(userId, active) {
  let shops = [];
  try {
    shops = asList(await apiGet('/shop/get/all'));
  } catch (_) {
    return;
  }
  const mine = shops.filter((shop) => Number(shop.userId) === Number(userId));
  for (const shop of mine) {
    try {
      await apiPost('/shop/update', {
        shopId: shop.shopId,
        shopName: shop.shopName,
        userId: shop.userId,
        addressId: shop.addressId,
        createdBy: shop.createdBy,
        gstId: shop.gstId,
        isActive: active,
        active,
      });
    } catch (_) {
      // Shop flag is secondary to the user status.
    }
  }
}

async function toggleUserActive(userId, makeActive) {
  const typeId = Number(document.body.dataset.userType || 0);
  const noun = (ROLE_PAGES[typeId] || {}).noun || 'user';
  const action = makeActive ? 'activate' : 'mark inactive';
  if (!confirm(`${action.charAt(0).toUpperCase() + action.slice(1)} this ${noun}?`)) return;

  try {
    const user = await apiGet(`/user/get/${userId}`);
    await apiPost('/user/update', {
      userId: user.userId,
      firstName: user.firstName,
      lastName: user.lastName,
      emailId: user.emailId,
      phoneNumber: user.phoneNumber,
      userTypeId: user.userTypeId || user.type?.userTypeId,
      addressId: user.addressId || user.address?.addressId,
      password: user.password,
      crateCount: user.crateCount,
      createdBy: user.createdBy,
      createdOn: user.createdOn,
      isActive: makeActive,
      active: makeActive,
    });
    if (Number(user.userTypeId || user.type?.userTypeId) === 3) {
      await setShopsActiveForUser(userId, makeActive);
    }
    await loadRoleUsers();
  } catch (error) {
    alert(error.message || 'Could not update status.');
  }
}

window.toggleUserActive = toggleUserActive;

async function loadRoleUsers() {
  const typeId = Number(document.body.dataset.userType || 2);
  const meta = ROLE_PAGES[typeId] || ROLE_PAGES[2];
  const tableBody = document.getElementById('users-table');
  const isSalesmanPage = typeId === 2;
  const colSpan = isSalesmanPage ? 10 : 6;
  tableBody.innerHTML = `<tr><td colspan="${colSpan}" class="text-center text-muted">Loading...</td></tr>`;

  let users = [];
  try {
    const data = await apiGet(`/user/get/usertype/${typeId}`);
    users = asList(data);
  } catch (err) {
    try {
      const all = await apiGet('/user/get/all');
      users = asList(all).filter((u) => Number(u.userTypeId || u.typeId || u.type?.userTypeId) === typeId);
    } catch (inner) {
      tableBody.innerHTML = `<tr><td colspan="${colSpan}" class="text-center text-danger">${inner.message || err.message}</td></tr>`;
      return;
    }
  }

  const walletsByUser = {};
  if (isSalesmanPage) {
    try {
      const wallets = asList(await apiGet('/admin/wallets/salesmen'));
      wallets.forEach((w) => { walletsByUser[Number(w.userId)] = w; });
      const totalsEl = document.getElementById('wallet-totals');
      if (totalsEl) {
        const walletSum = wallets.reduce((s, w) => s + Number(w.walletBalance || 0), 0);
        const pendingSum = wallets.reduce((s, w) => s + Number(w.pending || 0), 0);
        const receivedSum = wallets.reduce((s, w) => s + Number(w.received || 0), 0);
        totalsEl.innerHTML = `
          <div class="col-md-4">
            <div class="small-box text-bg-primary">
              <div class="inner"><h3>${formatInr(walletSum)}</h3><p>Current wallet (all salesmen)</p></div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="small-box text-bg-warning">
              <div class="inner"><h3>${formatInr(pendingSum)}</h3><p>Pending collections</p></div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="small-box text-bg-success">
              <div class="inner"><h3>${formatInr(receivedSum)}</h3><p>Received collections</p></div>
            </div>
          </div>`;
      }
    } catch (walletErr) {
      console.warn('Could not load salesman wallets', walletErr);
    }
  }

  if (!users.length) {
    tableBody.innerHTML = `<tr><td colspan="${colSpan}" class="text-center text-muted">${meta.empty}</td></tr>`;
    return;
  }

  tableBody.innerHTML = users.map((user) => {
    const id = user.userId;
    const name = user.firstName || '';
    const phone = user.phoneNumber || '';
    const email = user.emailId || '';
    const address = user.address?.fullAddress || '';
    const active = isActiveUser(user);
    const rowClass = active ? 'clickable-row' : 'clickable-row disabled-row';
    const action = active
      ? `<button type="button" class="btn btn-sm btn-outline-danger" onclick="event.stopPropagation(); toggleUserActive(${id}, false)">Mark inactive</button>`
      : `<button type="button" class="btn btn-sm btn-outline-success" onclick="event.stopPropagation(); toggleUserActive(${id}, true)">Activate</button>`;
    const wallet = walletsByUser[Number(id)] || {};
    const walletCols = isSalesmanPage
      ? `<td>${formatInr(wallet.walletBalance || 0)}</td>
         <td>${formatInr(wallet.pending || 0)}</td>
         <td>${formatInr(wallet.received || 0)}</td>`
      : '';
    const track = isSalesmanPage
      ? `<td><a class="btn btn-sm btn-primary" href="salesmandetail.html?id=${id}" onclick="event.stopPropagation()">Track</a></td>`
      : '';
    return `
      <tr class="${rowClass}">
        <td><a href="${isSalesmanPage ? 'salesmandetail.html?id=' + id : 'userdetails.html?id=' + id}" class="link-primary">${name}</a></td>
        <td><span class="badge bg-info text-dark">${typeLabel(user, typeId)}</span></td>
        <td>${phone}</td>
        <td>${email}</td>
        <td>${address}</td>
        <td>
          <span class="badge ${active ? 'bg-success' : 'bg-secondary'}">${active ? 'Active' : 'Inactive'}</span>
          <div class="mt-2">${action}</div>
        </td>
        ${walletCols}
        ${track}
      </tr>`;
  }).join('');
}

function searchTable() {
  const input = document.getElementById('searchInput');
  const filter = (input.value || '').toUpperCase();
  const table = document.getElementById('userTable');
  const tr = table.getElementsByTagName('tr');
  for (let i = 1; i < tr.length; i++) {
    const text = tr[i].textContent || '';
    tr[i].style.display = text.toUpperCase().indexOf(filter) > -1 ? '' : 'none';
  }
}

document.addEventListener('DOMContentLoaded', loadRoleUsers);
