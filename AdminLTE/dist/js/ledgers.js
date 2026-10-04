function mapLookup(map, ledgerId) {
  if (!map || typeof map !== 'object') return 0;
  const key = String(ledgerId);
  if (map[key] != null) return Number(map[key]) || 0;
  if (map[ledgerId] != null) return Number(map[ledgerId]) || 0;
  return 0;
}

function formatWhen(value) {
  if (!value) return '—';
  const text = String(value);
  return text.replace('T', ' ').slice(0, 19);
}

async function loadLedgers() {
  const tbody = document.getElementById('ledger-table-body');
  tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">Loading...</td></tr>';
  try {
    const data = unwrap(await apiGet('/admin/ledgers/get'));
    const ledgers = asList(data.ledger);
    const creditMap = unwrap(data.creditmap) || {};
    const debitMap = unwrap(data.debitmap) || {};
    if (!ledgers.length) {
      tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">No ledgers yet. They are created when an order is confirmed.</td></tr>';
      return;
    }
    tbody.innerHTML = ledgers.map((ledger) => {
      const id = ledger.ledgerId;
      const salesmanName = personName(ledger.salesman) || ('Salesman #' + ledger.salesmanId);
      const retailerName = personName(ledger.retailer) || ('Retailer #' + ledger.retailerId);
      const credit = mapLookup(creditMap, id);
      const debit = mapLookup(debitMap, id);
      return `
        <tr style="cursor:pointer" onclick="window.location.href='ledgerdetails.html?id=${id}'">
          <td>${id}</td>
          <td>${ledger.salesmanId}</td>
          <td><a href="salesmandetail.html?id=${ledger.salesmanId}" onclick="event.stopPropagation()">${salesmanName}</a></td>
          <td>${ledger.retailerId}</td>
          <td>${retailerName}</td>
          <td>${formatWhen(ledger.lastUpdated || ledger.createdOn)}</td>
          <td class="text-success">${formatInr(credit)}</td>
          <td class="text-danger">${formatInr(debit)}</td>
        </tr>`;
    }).join('');
  } catch (error) {
    tbody.innerHTML = `<tr><td colspan="8" class="text-center text-danger">${error.message}</td></tr>`;
  }
}

function searchTable() {
  const input = document.getElementById('searchInput');
  const filter = (input.value || '').toUpperCase();
  const table = document.getElementById('ledgerTable');
  const tr = table.getElementsByTagName('tr');
  for (let i = 1; i < tr.length; i++) {
    const text = tr[i].textContent || '';
    tr[i].style.display = text.toUpperCase().indexOf(filter) > -1 ? '' : 'none';
  }
}

document.addEventListener('DOMContentLoaded', loadLedgers);
