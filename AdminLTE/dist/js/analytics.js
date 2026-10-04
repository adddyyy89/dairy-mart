function emptyRow(cols, message) {
  return `<tr><td colspan="${cols}" class="text-muted">${message}</td></tr>`;
}

async function loadAnalytics() {
  const alertEl = document.getElementById('alertMessage');
  try {
    const data = unwrap(await apiGet('/admin/analytics/get'));
    document.getElementById('analytics-kpis').innerHTML = `
      <div class="col-md-3"><div class="small-box text-bg-primary"><div class="inner"><h3>${data.totalOrders || 0}</h3><p>Orders counted</p></div></div></div>
      <div class="col-md-3"><div class="small-box text-bg-info"><div class="inner"><h3>${data.totalUnits || 0}</h3><p>Units ordered</p></div></div></div>
      <div class="col-md-3"><div class="small-box text-bg-success"><div class="inner"><h3>${formatInr(data.totalCollected || 0)}</h3><p>Collected from stores</p></div></div></div>
      <div class="col-md-3"><div class="small-box text-bg-warning"><div class="inner"><h3>${data.cratesInSystem || 0}</h3><p>Crates in system</p></div></div></div>`;

    const products = asList(data.topProducts);
    document.getElementById('top-products').innerHTML = products.length
      ? products.map((r) => `<tr><td>${r.name || ''}</td><td>${r.detail || ''}</td><td>${r.count || 0}</td><td>${formatInr(r.amount || 0)}</td></tr>`).join('')
      : emptyRow(4, 'No order lines yet.');

    const stores = asList(data.storesByOrders);
    document.getElementById('stores-orders').innerHTML = stores.length
      ? stores.map((r) => `<tr><td>${r.name || ''}</td><td>${r.count || 0}</td><td>${r.detail || ''}</td></tr>`).join('')
      : emptyRow(3, 'No store orders yet.');

    const salesmen = asList(data.salesmenCollected);
    document.getElementById('salesmen-collected').innerHTML = salesmen.length
      ? salesmen.map((r) => `<tr><td>${r.name || ''}</td><td>${formatInr(r.amount || 0)}</td></tr>`).join('')
      : emptyRow(2, 'No collections yet.');

    const paid = asList(data.storesPaid);
    document.getElementById('stores-paid').innerHTML = paid.length
      ? paid.map((r) => `<tr><td>${r.name || ''}</td><td>${formatInr(r.amount || 0)}</td></tr>`).join('')
      : emptyRow(2, 'No store payments yet.');

    const pairs = asList(data.storeProducts);
    document.getElementById('store-products').innerHTML = pairs.length
      ? pairs.map((r) => `<tr><td>${r.name || ''}</td><td>${r.detail || ''}</td><td>${r.count || 0}</td><td>${Math.round(r.amount || 0)}</td></tr>`).join('')
      : emptyRow(4, 'No store product mix yet.');

    const crates = asList(data.crateLocations);
    document.getElementById('crate-locations').innerHTML = crates.length
      ? crates.map((r) => `<tr><td>${r.name || ''}</td><td>${r.detail || ''}</td><td>${r.count || 0}</td></tr>`).join('')
      : emptyRow(3, 'No crate holdings.');
  } catch (error) {
    alertEl.style.display = 'block';
    alertEl.className = 'alert alert-danger';
    alertEl.textContent = error.message || 'Could not load analytics.';
  }
}

document.addEventListener('DOMContentLoaded', loadAnalytics);
