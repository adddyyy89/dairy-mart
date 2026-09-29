function queryId() {
  return parseInt(new URLSearchParams(window.location.search).get('id') || '', 10);
}

function txWhen(tx) {
  const raw = tx.createdOn || tx.lastUpdated || '';
  return String(raw).replace('T', ' ').slice(0, 19) || '—';
}

function txMode(tx) {
  const type = tx.paymentType || {};
  return type.paymentTypeDesc || type.paymentTypeName || type.productTypeName || (tx.credit ? 'Collection' : 'Order');
}

async function loadLedgerDetail() {
  const ledgerId = queryId();
  const alertDiv = document.getElementById('alertMessage');
  if (!ledgerId) {
    alertDiv.className = 'alert alert-danger';
    alertDiv.textContent = 'Missing ledger id.';
    alertDiv.style.display = 'block';
    return;
  }
  try {
    const data = unwrap(await apiGet('/ledger/salesman/get/' + ledgerId));
    document.getElementById('retailerName').textContent = data.retailerName || 'Retailer';
    document.getElementById('retailerAddress').textContent = data.retailerAddress || '';
    const balance = Number(data.balance) || 0;
    const balanceEl = document.getElementById('ledgerBalance');
    balanceEl.textContent = formatInr(balance);
    balanceEl.classList.toggle('text-danger', balance < 0);
    balanceEl.classList.toggle('text-success', balance >= 0);

    const txs = asList(data.transactionsDTOS);
    const tbody = document.getElementById('tx-body');
    if (!txs.length) {
      tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">No transactions.</td></tr>';
      return;
    }
    tbody.innerHTML = txs.map((tx) => {
      const credit = tx.credit === true;
      return `
        <tr>
          <td>${txWhen(tx)}</td>
          <td>${txMode(tx)}</td>
          <td><span class="badge ${credit ? 'bg-success' : 'bg-danger'}">${credit ? 'Credit' : 'Debit'}</span></td>
          <td class="${credit ? 'text-success' : 'text-danger'}">${credit ? '+' : '-'}${formatInr(tx.amount)}</td>
        </tr>`;
    }).join('');
  } catch (error) {
    alertDiv.className = 'alert alert-danger';
    alertDiv.textContent = error.message;
    alertDiv.style.display = 'block';
  }
}

async function recordCollection(e) {
  e.preventDefault();
  const ledgerId = queryId();
  const amount = Number(document.getElementById('amount').value);
  const paymentTypeId = Number(document.getElementById('paymentTypeId').value) || 1;
  const alertDiv = document.getElementById('alertMessage');
  const session = JSON.parse(sessionStorage.getItem('user') || '{}');
  if (!(amount > 0)) {
    alertDiv.className = 'alert alert-danger';
    alertDiv.textContent = 'Enter a valid amount.';
    alertDiv.style.display = 'block';
    return;
  }
  try {
    await apiPost('/ledger/salesman/update', {
      ledgerId,
      amount,
      credit: true,
      debit: false,
      paymentTypeId,
      createdBy: session.userId || 1,
    });
    document.getElementById('amount').value = '';
    alertDiv.className = 'alert alert-success';
    alertDiv.textContent = 'Collection recorded.';
    alertDiv.style.display = 'block';
    await loadLedgerDetail();
  } catch (error) {
    alertDiv.className = 'alert alert-danger';
    alertDiv.textContent = error.message;
    alertDiv.style.display = 'block';
  }
}

document.addEventListener('DOMContentLoaded', () => {
  loadLedgerDetail();
  document.getElementById('collectionForm').addEventListener('submit', recordCollection);
});
