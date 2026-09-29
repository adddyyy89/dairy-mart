document.addEventListener('DOMContentLoaded', async function () {
  const params = new URLSearchParams(window.location.search);
  const id = params.get('id');
  const card = document.querySelector('.order-card');
  if (!id) {
    if (card) {
      card.innerHTML = '<div class="alert alert-danger">Missing order id.</div>';
    }
    return;
  }
  try {
    const data = unwrap(await apiGet('/retailorder/get/' + id));
    renderOrderData(data);
  } catch (error) {
    if (card) {
      card.innerHTML = `<div class="alert alert-danger"><strong>Error:</strong> ${error.message || SERVER_DOWN_MESSAGE}</div>`;
    }
  }
});

function renderOrderData(data) {
  data = unwrap(data);
  const status = unwrap(data.status || {});
  const retailer = unwrap(data.retailer || {});
  const owner = unwrap(retailer.owner || {});
  const branch = unwrap(data.branch || {});
  const address = unwrap(retailer.address || {});
  const updateText = (id, val) => {
    const el = document.getElementById(id);
    if (el) el.textContent = val == null ? '' : String(val);
  };

  updateText('orderId', data.orderId);
  updateText('orderDate', data.orderDate);
  const statusEl = document.getElementById('statusDesc');
  if (statusEl) {
    statusEl.innerHTML = `<span class="badge bg-success">${status.statusDesc || ('Status ' + (data.orderStatusId || ''))}</span>`;
  }
  updateText('branchName', branch.branchName);
  const retailerEl = document.getElementById('retailerName');
  if (retailerEl) {
    const name = retailer.shopName || ('Shop #' + (data.retailerId || ''));
    retailerEl.innerHTML = owner.userId
      ? `<a href="userdetails.html?id=${owner.userId}">${name}</a>`
      : name;
  }
  updateText('retailerAddress', address.fullAddress);

  const salesmanEl = document.getElementById('salesmanName');
  try {
    const map = JSON.parse(sessionStorage.getItem('retailerSalesmanDataMap') || 'null');
    const row = map && map[data.retailerId];
    if (salesmanEl && row) {
      salesmanEl.innerHTML = `<a href="userdetails.html?id=${row.userId}">${[row.firstName, row.lastName].filter(Boolean).join(' ')}</a>`;
    } else if (salesmanEl) {
      salesmanEl.textContent = '';
    }
  } catch (_) {
    if (salesmanEl) salesmanEl.textContent = '';
  }

  const tableBody = document.getElementById('orderItemsTable');
  const lines = asList(data.orderDetails);
  if (tableBody) {
    tableBody.innerHTML = lines.map((item) => `
      <tr>
        <td><strong>${item.productCode || ''}</strong></td>
        <td>${item.quantity || ''}</td>
        <td>${item.unit || ''}</td>
        <td>₹${item.saleRate || ''}</td>
        <td>₹${item.purchaseRate || ''}</td>
      </tr>`).join('');
  }
}
