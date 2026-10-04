/** Human-readable order status from the dashboard/order payload. */
function orderStatusLabel(order) {
  const nested = unwrap(order.status || order.orderStatus || {});
  return nested.statusDesc
    || order.statusDesc
    || statusIdLabel(order.orderStatusId || nested.statusId);
}

function statusIdLabel(id) {
  switch (Number(id)) {
    case 1: return 'NEW';
    case 2: return 'CONFIRMED';
    case 3: return 'REJECTED';
    case 4: return 'DISPATCHED';
    case 5: return 'DELIVERED';
    case 6: return 'RETURNED';
    case 7: return 'CANCELLED';
    default: return id ? ('Status ' + id) : '';
  }
}

function formatWhen(value) {
  if (!value) return '';
  return String(value).replace('T', ' ').slice(0, 19);
}

/** Updates the count text and the matching progress bar width. */
function setOrderStatusBar(labelId, barId, count, total) {
  const label = document.getElementById(labelId);
  const bar = document.getElementById(barId);
  const n = Number(count || 0);
  const t = Number(total || 0);
  if (label) label.innerHTML = '<b>' + n + '</b>/' + t;
  if (bar) bar.style.width = (t ? Math.round((n / t) * 100) : 0) + '%';
}

async function getData() {
  try {
    const json = unwrap(await apiGet('/admin/dashboard/get'));
    document.getElementById('total-retailers').innerText = json.totalRetailers ?? 0;
    document.getElementById('total-salesman').innerText = json.totalSalesman ?? 0;
    document.getElementById('total-orders').innerText = json.totalTodaysOrder ?? 0;
    document.getElementById('total-transactions').innerText = json.totalTodaysTransactions ?? 0;

    const orders = asList(json.latestOrders);
    const totalOrders = Number(json.totalTodaysOrder || orders.length || 0);
    setOrderStatusBar('order-new', 'order-new-bar', json.statusNew, totalOrders);
    setOrderStatusBar('order-confirmed', 'order-confirmed-bar', json.statusConfirmed, totalOrders);
    setOrderStatusBar('order-rejected', 'order-rejected-bar', json.statusRejected, totalOrders);
    setOrderStatusBar('order-dispatched', 'order-dispatched-bar', json.statusDispatched, totalOrders);
    setOrderStatusBar('order-delivered', 'order-delivered-bar', json.statusDelivered, totalOrders);

    const tableBody = document.getElementById('latest-orders-body');
    tableBody.innerHTML = orders.length
      ? orders.map((order) => `
          <tr>
            <td><a href="./pages/orderdetails.html?id=${order.orderId}">${order.orderId}</a></td>
            <td>${order.retailer?.shopName || ('Shop #' + (order.retailerId || ''))}</td>
            <td><span class="badge text-bg-info">${orderStatusLabel(order)}</span></td>
          </tr>`).join('')
      : '<tr><td colspan="3" class="text-muted">No orders today.</td></tr>';

    const txBody = document.getElementById('latest-transactions-body');
    const txs = asList(json.latestTransactions);
    if (txBody) {
      txBody.innerHTML = txs.length
        ? txs.map((tx) => `
            <tr>
              <td>${tx.transactionId || tx.ledgerId || ''}</td>
              <td>${personName(tx.retailer) || ('#' + (tx.retailerId || ''))}</td>
              <td><span class="badge text-bg-success">${tx.credit ? 'Credit' : (tx.debit ? 'Debit' : '')}</span></td>
              <td>${formatInr(tx.amount)}</td>
            </tr>`).join('')
        : '<tr><td colspan="4" class="text-muted">No ledger activity today.</td></tr>';
    }
  } catch (error) {
    const tableBody = document.getElementById('latest-orders-body');
    if (tableBody) {
      tableBody.innerHTML = `<tr><td colspan="3" class="text-danger">${error.message || SERVER_DOWN_MESSAGE}</td></tr>`;
    }
  }

  try {
    const activity = asList(await apiGet('/admin/activity')).slice(0, 8);
    const body = document.getElementById('recent-activity-body');
    if (body) {
      body.innerHTML = activity.length
        ? activity.map((n) => `<tr><td>${n.kind || ''}</td><td>${n.title || ''}${n.message ? ' — ' + n.message : ''}</td><td>${formatWhen(n.createdOn)}</td></tr>`).join('')
        : '<tr><td colspan="3" class="text-muted">No activity recorded yet.</td></tr>';
    }
  } catch (error) {
    const body = document.getElementById('recent-activity-body');
    if (body) {
      body.innerHTML = `<tr><td colspan="3" class="text-danger">${error.message || SERVER_DOWN_MESSAGE}</td></tr>`;
    }
  }

  try {
    const sessions = unwrap(await apiGet('/admin/sessions'));
    const online = asList(sessions.online);
    const list = document.getElementById('online-users-list');
    if (list) {
      list.innerHTML = online.length
        ? online.map((u) => `<li class="list-group-item">${u.name || u.phoneNumber} <span class="text-muted">(${u.roleLabel || ''})</span></li>`).join('')
        : '<li class="list-group-item text-muted">Nobody is online.</li>';
    }
  } catch (error) {
    const list = document.getElementById('online-users-list');
    if (list) {
      list.innerHTML = `<li class="list-group-item text-danger">${error.message || SERVER_DOWN_MESSAGE}</li>`;
    }
  }
}

getData();
