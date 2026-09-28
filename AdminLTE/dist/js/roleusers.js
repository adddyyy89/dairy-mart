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
  tableBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Loading...</td></tr>';

  let users = [];
  try {
    const data = await apiGet(`/user/get/usertype/${typeId}`);
    users = asList(data);
  } catch (err) {
    try {
      const all = await apiGet('/user/get/all');
      users = asList(all).filter((u) => Number(u.userTypeId || u.typeId || u.type?.userTypeId) === typeId);
    } catch (inner) {
      tableBody.innerHTML = `<tr><td colspan="6" class="text-center text-danger">${inner.message || err.message}</td></tr>`;
      return;
    }
  }

  if (!users.length) {
    tableBody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">${meta.empty}</td></tr>`;
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
    return `
      <tr class="${rowClass}">
        <td><a href="userdetails.html?id=${id}" class="link-primary">${name}</a></td>
        <td><span class="badge bg-info text-dark">${typeLabel(user, typeId)}</span></td>
        <td>${phone}</td>
        <td>${email}</td>
        <td>${address}</td>
        <td>
          <span class="badge ${active ? 'bg-success' : 'bg-secondary'}">${active ? 'Active' : 'Inactive'}</span>
          <div class="mt-2">${action}</div>
        </td>
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
