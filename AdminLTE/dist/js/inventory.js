function showInvAlert(message, ok) {
  const el = document.getElementById('alertMessage');
  if (!el) return;
  el.style.display = 'block';
  el.className = 'alert ' + (ok ? 'alert-success' : 'alert-danger');
  el.textContent = message;
}

async function loadInventory() {
  const select = document.getElementById('branchSelect');
  const body = document.getElementById('inventory-body');
  const branchId = Number(select.value || 0);
  if (!branchId) {
    body.innerHTML = '<tr><td colspan="4" class="text-muted">Select a branch.</td></tr>';
    return;
  }
  try {
    const rows = asList(await apiGet('/inventory/branch/' + branchId));
    body.innerHTML = rows.length
      ? rows.map((r) => `
          <tr>
            <td>${r.productName || ''}</td>
            <td>${r.productCode || ''}</td>
            <td style="max-width:140px">
              <input type="number" min="0" class="form-control form-control-sm" id="qty-${r.productId}" value="${r.quantity || 0}">
            </td>
            <td>
              <button class="btn btn-sm btn-primary" type="button" onclick="saveInventory(${branchId}, ${r.productId})">Save</button>
            </td>
          </tr>`).join('')
      : '<tr><td colspan="4" class="text-muted">No products.</td></tr>';
  } catch (error) {
    showInvAlert(error.message || 'Could not load inventory. Run docs/sql/create_branch_inventory.sql if the table is missing.', false);
  }
}

async function saveInventory(branchId, productId) {
  const qty = Number(document.getElementById('qty-' + productId).value || 0);
  try {
    await apiPost('/inventory/set', { branchId, productId, quantity: qty });
    showInvAlert('Inventory saved.', true);
    await loadInventory();
  } catch (error) {
    showInvAlert(error.message || 'Could not save inventory.', false);
  }
}

async function loadBranches() {
  const select = document.getElementById('branchSelect');
  const branches = asList(await apiGet('/branch/get/all'));
  select.innerHTML = branches.map((b) =>
    `<option value="${b.branchId}">${b.branchName || ('Branch ' + b.branchId)}</option>`
  ).join('');
  const preferred = Number((typeof dmSettings === 'function' ? dmSettings().defaultBranchId : 7) || 7);
  if (branches.some((b) => Number(b.branchId) === preferred)) {
    select.value = String(preferred);
  } else if (branches.some((b) => Number(b.branchId) === 7)) {
    select.value = '7';
  }
  select.onchange = loadInventory;
  await loadInventory();
}

window.saveInventory = saveInventory;
document.addEventListener('DOMContentLoaded', () => {
  loadBranches().catch((error) => showInvAlert(error.message || 'Could not load branches.', false));
});
