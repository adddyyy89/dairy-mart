function salesmanId() {
  return parseInt(new URLSearchParams(window.location.search).get('id') || '', 10);
}

function todayDdMmYyyy() {
  const d = new Date();
  const dd = String(d.getDate()).padStart(2, '0');
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  return `${dd}-${mm}-${d.getFullYear()}`;
}

function orderStatus(order) {
  return order.status?.statusDesc || order.orderStatus?.statusDesc || ('Status ' + (order.orderStatusId || ''));
}

function shopLabel(assignment) {
  return assignment.retailer?.shopName
    || assignment.shopName
    || ('Shop #' + (assignment.retailerId || assignment.shopId || ''));
}

let map;
let mapLayer;

function drawTrack(points) {
  const el = document.getElementById('map');
  if (!window.L || !el) return;
  if (!map) {
    map = L.map('map').setView([20.5937, 78.9629], 5);
    L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
      attribution: 'Tiles &copy; Esri',
      maxZoom: 19,
    }).addTo(map);
  }
  if (mapLayer) {
    map.removeLayer(mapLayer);
    mapLayer = null;
  }
  const coords = points
    .map((p) => [Number(p.latitude), Number(p.longitude)])
    .filter((pair) => Number.isFinite(pair[0]) && Number.isFinite(pair[1]));
  const status = document.getElementById('gpsStatus');
  if (!coords.length) {
    status.textContent = 'No GPS pings for today.';
    return;
  }
  mapLayer = L.layerGroup();
  coords.forEach((latlng, i) => {
    L.marker(latlng).bindPopup(i === coords.length - 1 ? 'Latest' : ('Ping ' + (i + 1))).addTo(mapLayer);
  });
  if (coords.length > 1) {
    L.polyline(coords, { color: '#0d6efd' }).addTo(mapLayer);
  }
  mapLayer.addTo(map);
  map.fitBounds(coords, { padding: [24, 24] });
  setTimeout(() => map.invalidateSize(), 250);
  const last = points[points.length - 1];
  status.textContent = `Today: ${coords.length} ping(s). Last at ${String(last.timestamp || '').replace('T', ' ').slice(0, 19)}`;
}

async function loadSalesman() {
  const id = salesmanId();
  const alertDiv = document.getElementById('alertMessage');
  if (!id) {
    alertDiv.className = 'alert alert-danger';
    alertDiv.textContent = 'Missing salesman id.';
    alertDiv.style.display = 'block';
    return;
  }

  const [user, dashboard, ledgerDash, assignments, orders, crates, tracking] = await Promise.all([
    apiGet('/user/get/' + id).catch(() => ({})),
    apiGet('/salesman/dashboard/get/' + id).catch(() => ({})),
    apiGet('/salesman/ledger/dashboard/get/' + id).catch(() => ({})),
    apiGet('/salesmantoretail/get/assignment/salesman/' + id).catch(() => []),
    apiGet('/retailorder/get/salesman/' + id).catch(() => []),
    apiGet('/crate/get/user/' + id).catch(() => []),
    apiGet('/tracking/get?userId=' + id + '&date=' + encodeURIComponent(todayDdMmYyyy())).catch(() => []),
  ]);

  const dash = unwrap(dashboard);
  const name = dash.salesmanname || personName(user) || ('Salesman #' + id);
  document.getElementById('salesmanName').textContent = name;
  document.getElementById('salesmanPhone').textContent = dash.salesmanphonenumber || user.phoneNumber || '';
  document.getElementById('editProfile').href = 'userdetails.html?id=' + id;
  document.getElementById('statWallet').textContent = formatInr(dash.walletbalance);
  document.getElementById('statOrders').textContent = dash.ordersplaced ?? asList(orders).length;
  document.getElementById('statCrates').textContent = dash.cratesassigned ?? 0;

  const crateRows = asList(crates);
  if (crateRows.length) {
    const latest = crateRows[0];
    document.getElementById('statCrates').textContent = latest.crateCount ?? dash.cratesassigned ?? 0;
    document.getElementById('statEngaged').textContent = latest.crateReceived ?? '—';
  } else {
    document.getElementById('statEngaged').textContent = '—';
  }

  const ledger = unwrap(ledgerDash);
  document.getElementById('statOutstanding').textContent = formatInr(ledger.outstanding);
  document.getElementById('statLedgerWallet').textContent = formatInr(ledger.walletbalance);

  const shops = asList(assignments).filter((a) => a.active !== false && a.isActive !== false);
  const shopBody = document.getElementById('shops-body');
  shopBody.innerHTML = shops.length
    ? shops.map((a) => `<tr><td>${shopLabel(a)}</td><td>${a.vehicleNumber || '—'}</td><td>${a.retailerId}</td></tr>`).join('')
    : '<tr><td colspan="3" class="text-muted">No retailers assigned.</td></tr>';

  const ledgerRows = asList(ledger.ledgersummary);
  const ledgerBody = document.getElementById('ledgers-body');
  ledgerBody.innerHTML = ledgerRows.length
    ? ledgerRows.map((row) => {
        const l = row.ledger || {};
        const rName = personName(l.retailer) || ('Retailer #' + (l.retailerId || ''));
        return `<tr style="cursor:pointer" onclick="window.location.href='ledgerdetails.html?id=${row.ledgerId}'">
          <td>${row.ledgerId}</td><td>${rName}</td><td>${formatInr(row.amount)}</td></tr>`;
      }).join('')
    : '<tr><td colspan="3" class="text-muted">No ledgers yet.</td></tr>';

  const orderRows = asList(orders);
  const orderBody = document.getElementById('orders-body');
  orderBody.innerHTML = orderRows.length
    ? orderRows.map((o) => {
        const shop = o.retailer?.shopName || ('Shop #' + o.retailerId);
        return `<tr style="cursor:pointer" onclick="window.location.href='orderdetails.html?id=${o.orderId}'">
          <td>${o.orderId}</td><td>${shop}</td><td>${orderStatus(o)}</td>
          <td>${String(o.orderDate || o.createdon || '').slice(0, 10)}</td></tr>`;
      }).join('')
    : '<tr><td colspan="4" class="text-muted">No orders.</td></tr>';

  drawTrack(asList(tracking));

  const qtyInput = document.getElementById('assignCrateQty');
  const assignBtn = document.getElementById('assignCrateBtn');
  const returnBtn = document.getElementById('returnCrateBtn');
  if (assignBtn) {
    assignBtn.onclick = async () => {
      const qty = Number(qtyInput && qtyInput.value || 0);
      try {
        await apiPost('/crate/assign', { salesmanId: id, quantity: qty });
        await loadSalesman();
      } catch (error) {
        alert(error.message || 'Could not assign crates.');
      }
    };
  }
  if (returnBtn) {
    returnBtn.onclick = async () => {
      const qty = Number(qtyInput && qtyInput.value || 0);
      try {
        await apiPost('/crate/branch/return', { salesmanId: id, quantity: qty });
        await loadSalesman();
      } catch (error) {
        alert(error.message || 'Could not return crates.');
      }
    };
  }
}

document.addEventListener('DOMContentLoaded', () => {
  loadSalesman().catch((error) => {
    const alertDiv = document.getElementById('alertMessage');
    alertDiv.className = 'alert alert-danger';
    alertDiv.textContent = error.message || 'Could not load salesman.';
    alertDiv.style.display = 'block';
  });
});
