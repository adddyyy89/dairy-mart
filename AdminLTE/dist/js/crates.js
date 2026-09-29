function showCrateAlert(message, ok) {
  const el = document.getElementById('alertMessage');
  if (!el) return;
  el.style.display = 'block';
  el.className = 'alert ' + (ok ? 'alert-success' : 'alert-danger');
  el.textContent = message;
}

async function loadCrates() {
  const totals = document.getElementById('crate-totals');
  const salesBody = document.getElementById('salesmen-crates');
  const storeBody = document.getElementById('store-crates');
  try {
    const data = unwrap(await apiGet('/crate/summary'));
    const total = Number(data.totalInSystem || 0);
    const branch = Number(data.atBranch || 0);
    const withMen = Number(data.withSalesmen || 0);
    const stores = Number(data.atStores || 0);
    totals.innerHTML = `
      <div class="col-md-3"><div class="small-box text-bg-primary"><div class="inner"><h3>${total}</h3><p>In system</p></div></div></div>
      <div class="col-md-3"><div class="small-box text-bg-success"><div class="inner"><h3>${branch}</h3><p>At branch</p></div></div></div>
      <div class="col-md-3"><div class="small-box text-bg-warning"><div class="inner"><h3>${withMen}</h3><p>With salesmen</p></div></div></div>
      <div class="col-md-3"><div class="small-box text-bg-info"><div class="inner"><h3>${stores}</h3><p>At stores</p></div></div></div>`;

    const salesmanRows = asList(data.salesmen);
    salesBody.innerHTML = salesmanRows.length
      ? salesmanRows.map((s) => `
          <tr>
            <td><a href="salesmandetail.html?id=${s.userId}">${s.name || ('#' + s.userId)}</a></td>
            <td>${s.phoneNumber || ''}</td>
            <td>${s.holding || 0}</td>
            <td>${s.sentToStores || 0}</td>
            <td>${s.returnedToBranch || 0}</td>
            <td>
              <div class="input-group input-group-sm" style="max-width:260px">
                <input type="number" min="1" class="form-control" id="assign-${s.userId}" value="1">
                <button class="btn btn-primary" type="button" onclick="assignCrates(${s.userId})">Assign</button>
                <button class="btn btn-outline-secondary" type="button" onclick="returnCrates(${s.userId})">Return</button>
              </div>
            </td>
          </tr>`).join('')
      : '<tr><td colspan="6" class="text-muted">No salesmen.</td></tr>';

    const storeRows = asList(data.stores);
    storeBody.innerHTML = storeRows.length
      ? storeRows.map((s) => `
          <tr>
            <td>${s.name || ('#' + s.userId)}</td>
            <td>${s.phoneNumber || ''}</td>
            <td>${s.sentToStores || 0}</td>
            <td>${s.atStore || 0}</td>
            <td>${s.returnedFromStore || 0}</td>
          </tr>`).join('')
      : '<tr><td colspan="5" class="text-muted">No crates currently at stores.</td></tr>';
  } catch (error) {
    showCrateAlert(error.message || 'Could not load crate summary.', false);
  }
}

async function addCratesToPool() {
  const qty = Number(document.getElementById('poolQty').value || 0);
  try {
    await apiPost('/crate/pool/add', { quantity: qty });
    showCrateAlert('Crates added to the branch.', true);
    await loadCrates();
  } catch (error) {
    showCrateAlert(error.message || 'Could not add crates.', false);
  }
}

async function assignCrates(salesmanId) {
  const input = document.getElementById('assign-' + salesmanId);
  const qty = Number(input && input.value || 0);
  try {
    await apiPost('/crate/assign', { salesmanId, quantity: qty });
    showCrateAlert('Crates assigned to salesman.', true);
    await loadCrates();
  } catch (error) {
    showCrateAlert(error.message || 'Could not assign crates.', false);
  }
}

async function returnCrates(salesmanId) {
  const input = document.getElementById('assign-' + salesmanId);
  const qty = Number(input && input.value || 0);
  try {
    await apiPost('/crate/branch/return', { salesmanId, quantity: qty });
    showCrateAlert('Crates returned to the branch.', true);
    await loadCrates();
  } catch (error) {
    showCrateAlert(error.message || 'Could not return crates.', false);
  }
}

window.addCratesToPool = addCratesToPool;
window.assignCrates = assignCrates;
window.returnCrates = returnCrates;
document.addEventListener('DOMContentLoaded', loadCrates);
