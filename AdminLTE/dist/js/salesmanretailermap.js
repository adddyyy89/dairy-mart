function currentUser() {
  try { return JSON.parse(sessionStorage.getItem('user') || '{}'); } catch (_) { return {}; }
}

function isActiveFlag(obj) {
  if (!obj) return true;
  if (obj.isActive === false || obj.active === false) return false;
  return true;
}

function salesmanName(user) {
  if (!user) return 'Unknown salesman';
  return `${user.firstName || ''} ${user.lastName || ''}`.trim() || (`User #${user.userId}`);
}

function shopLabel(shop) {
  if (!shop) return 'Unknown shop';
  const owner = shop.owner ? ` (${shop.owner.firstName || ''} ${shop.owner.lastName || ''})`.trim() : '';
  return `${shop.shopName || ('Shop #' + shop.shopId)}${owner}`;
}

function showAlert(kind, message) {
  const el = document.getElementById('alertMessage');
  el.className = `alert alert-${kind} mt-3`;
  el.textContent = message;
  el.style.display = 'block';
}

function formatDate(value) {
  if (!value) return '—';
  if (typeof value === 'string') return value.slice(0, 10);
  if (value.year) return `${value.year}-${String(value.month).padStart(2, '0')}-${String(value.day).padStart(2, '0')}`;
  return String(value);
}

let shops = [];
let salesmen = [];
let assignments = [];
let branches = [];

async function loadShops() {
  try {
    const data = await apiGet('/shop/get/all');
    const list = asList(data);
    if (list.length) return list;
  } catch (error) {
    console.warn('shop/get/all failed', error);
  }

  const shopsById = new Map();
  try {
    const retailers = asList(await apiGet('/user/get/usertype/3'));
    for (const retailer of retailers) {
      const userId = retailer.userId;
      if (!userId) continue;
      try {
        const theirs = asList(await apiGet('/shop/get/user/' + userId));
        theirs.forEach((shop) => {
          if (shop && shop.shopId != null) shopsById.set(Number(shop.shopId), shop);
        });
      } catch (_) {}
    }
  } catch (_) {}
  return Array.from(shopsById.values());
}

async function loadMappingPage() {
  const tableBody = document.getElementById('mappingTableBody');
  tableBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Loading...</td></tr>';

  try {
    assignments = asList(await apiGet('/salesmantoretail/get/all'));
  } catch (error) {
    assignments = [];
    console.warn(error);
  }

  shops = await loadShops();

  try {
    salesmen = asList(await apiGet('/user/get/usertype/2'));
  } catch (_) {
    try {
      salesmen = asList(await apiGet('/user/get/all')).filter((u) => Number(u.userTypeId || u.type?.userTypeId) === 2);
    } catch (_) {
      salesmen = [];
    }
  }

  try {
    branches = asList(await apiGet('/branch/get/all'));
  } catch (_) {
    branches = [];
  }

  if (!shops.length) {
    showAlert('warning', 'No retailer shops found. Add a retailer with a shop name first.');
  }

  fillDropdowns();
  renderTable();
}

function fillDropdowns() {
  const salesmanSelect = document.getElementById('salesmanSelect');
  const retailerSelect = document.getElementById('retailerSelect');
  const branchSelect = document.getElementById('branchSelect');

  salesmanSelect.innerHTML = '<option value="" selected disabled>Choose a salesman...</option>';
  salesmen.filter(isActiveFlag).forEach((user) => {
    salesmanSelect.add(new Option(salesmanName(user), user.userId));
  });

  const assignedShopIds = new Set(
    assignments.filter(isActiveFlag).map((row) => Number(row.retailerId || row.retailer?.shopId))
  );

  retailerSelect.innerHTML = '<option value="" selected disabled>Choose a retailer shop...</option>';
  shops.forEach((shop) => {
    const already = assignedShopIds.has(Number(shop.shopId));
    const inactive = shop.isActive === false || shop.active === false;
    const label = shopLabel(shop)
      + (already ? ' (already assigned)' : '')
      + (inactive ? ' (inactive)' : '');
    retailerSelect.add(new Option(label, shop.shopId));
  });

  branchSelect.innerHTML = '';
  if (branches.length) {
    branches.forEach((branch) => {
      branchSelect.add(new Option(branch.branchName || `Branch ${branch.branchId}`, branch.branchId));
    });
  } else {
    branchSelect.add(new Option('Default branch (7)', 7));
  }
  branchSelect.value = branches.some((b) => Number(b.branchId) === 7) ? '7' : (branchSelect.options[0]?.value || '7');
}

function renderTable() {
  const tableBody = document.getElementById('mappingTableBody');
  if (!assignments.length) {
    tableBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No salesman–retailer assignments yet.</td></tr>';
    return;
  }

  tableBody.innerHTML = assignments.map((item) => {
    const salesman = item.salesman || {};
    const shop = item.retailer || {};
    const active = isActiveFlag(item);
    const sid = item.salesmanId;
    const rid = item.retailerId;
    return `
      <tr class="${active ? '' : 'table-secondary'}">
        <td>
          <div class="fw-bold">${salesmanName(salesman)}</div>
          <small class="text-muted">${salesman.phoneNumber || ''}</small>
        </td>
        <td>
          <div class="fw-bold">${shop.shopName || ('Shop #' + rid)}</div>
          <small class="text-muted">${shop.address?.fullAddress || ''}</small>
        </td>
        <td>${item.vehicleNumber || '—'}</td>
        <td>${formatDate(item.createdOn)}</td>
        <td>
          <span class="badge ${active ? 'bg-success' : 'bg-secondary'}">${active ? 'Active' : 'Inactive'}</span>
        </td>
        <td class="text-center">
          ${active ? `<button type="button" class="btn btn-sm btn-outline-danger" onclick="deleteMapping(${sid}, ${rid})"><i class="bi bi-trash"></i></button>` : ''}
        </td>
      </tr>`;
  }).join('');
}

document.addEventListener('DOMContentLoaded', () => {
  loadMappingPage();

  document.getElementById('mappingForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const salesmanId = parseInt(document.getElementById('salesmanSelect').value, 10);
    const retailerId = parseInt(document.getElementById('retailerSelect').value, 10);
    const branchId = parseInt(document.getElementById('branchSelect').value, 10) || 7;
    const vehicleNumber = document.getElementById('vehicleNumber').value.trim();
    const user = currentUser();

    if (!salesmanId || !retailerId) {
      showAlert('warning', 'Select both a salesman and a retailer shop.');
      return;
    }

    try {
      await apiPost('/salesmantoretail/assign', {
        salesmanId,
        retailerId,
        vehicleNumber,
        createdBy: user.userId || 1,
        isActive: true,
        branchId,
      });
      showAlert('success', 'Retailer assigned to salesman.');
      document.getElementById('mappingForm').reset();
      await loadMappingPage();
    } catch (error) {
      showAlert('danger', error.message);
    }
  });
});

async function deleteMapping(salesmanId, retailerId) {
  if (!confirm('Remove this salesman–retailer assignment?')) return;
  try {
    await apiPost('/salesmantoretail/delete', { salesmanId, retailerId });
    showAlert('success', 'Assignment removed.');
    await loadMappingPage();
  } catch (error) {
    showAlert('danger', error.message);
  }
}

window.deleteMapping = deleteMapping;
