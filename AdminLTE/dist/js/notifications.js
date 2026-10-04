async function loadNotificationsPage() {
  const tableBody = document.getElementById('notifications-table');
  if (!tableBody) return;
  const userId = typeof ensureSessionUserId === 'function'
    ? await ensureSessionUserId()
    : (typeof sessionUserId === 'function' ? sessionUserId() : 0);
  if (userId == null) {
    tableBody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">Could not resolve your admin user id. Log out and sign in with a real admin account.</td></tr>';
    return;
  }
  tableBody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">Loading...</td></tr>';
  try {
    const list = asList(await apiGet('/notification/get/' + userId));
    if (!list.length) {
      tableBody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">No notifications yet.</td></tr>';
      return;
    }
    tableBody.innerHTML = list.map((n) => {
      const unread = !n.isRead && !n.read;
      const when = n.createdOn ? new Date(n.createdOn).toLocaleString() : '';
      const kindMap = {
        LEDGER: 'Ledger',
        ORDER: 'Order',
        HOLIDAY: 'Holiday',
        CLOSURE: 'Closure',
        SALE: 'Sale',
        ANNOUNCEMENT: 'Notice',
        LOGIN: 'Login',
        LOGOUT: 'Logout',
        USER: 'User',
        CRATE: 'Crate',
      };
      const kind = kindMap[n.kind] || n.kind || 'Notice';
      return `<tr class="${unread ? 'table-warning' : ''}">
        <td>${kind}</td>
        <td><strong>${n.title || ''}</strong><div class="text-secondary">${n.message || ''}</div></td>
        <td>${when}</td>
        <td>${unread
          ? `<button class="btn btn-sm btn-outline-primary" onclick="markNotificationRead(${n.notificationId})">Mark read</button>`
          : '<span class="badge bg-secondary">Read</span>'}</td>
      </tr>`;
    }).join('');
  } catch (error) {
    tableBody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">${error.message || 'Could not load notifications.'}</td></tr>`;
  }
}

async function markNotificationRead(id) {
  try {
    await apiPost('/notification/read/' + id, {});
    await loadNotificationsPage();
    if (typeof refreshHeaderNotifications === 'function') refreshHeaderNotifications();
  } catch (error) {
    alert(error.message || 'Could not mark as read.');
  }
}

window.markNotificationRead = markNotificationRead;

async function sendBroadcast() {
  const alertEl = document.getElementById('broadcastAlert');
  const title = (document.getElementById('broadcastTitle').value || '').trim();
  const message = (document.getElementById('broadcastMessage').value || '').trim();
  const when = document.getElementById('broadcastWhen').value;
  const body = {
    title,
    message,
    kind: document.getElementById('broadcastKind').value,
    audience: document.getElementById('broadcastAudience').value,
  };
  if (when) {
    const millis = new Date(when).getTime();
    if (!Number.isFinite(millis)) {
      alertEl.style.display = 'block';
      alertEl.className = 'alert alert-danger';
      alertEl.textContent = 'That send-later time is not valid.';
      return;
    }
    body.scheduledForMillis = millis;
  }
  try {
    const result = unwrap(await apiPost('/notification/broadcast', body));
    if (alertEl) {
      alertEl.style.display = 'block';
      alertEl.className = 'alert alert-success';
      alertEl.textContent = result.scheduled
        ? 'Scheduled for ' + new Date(Number(result.scheduledForMillis || 0)).toLocaleString() + '.'
        : 'Sent to ' + (result.sent || 0) + ' user(s).';
    }
    document.getElementById('broadcastTitle').value = '';
    document.getElementById('broadcastMessage').value = '';
    document.getElementById('broadcastWhen').value = '';
    await loadNotificationsPage();
    await loadScheduledNotices();
    if (typeof refreshHeaderNotifications === 'function') refreshHeaderNotifications();
  } catch (error) {
    if (alertEl) {
      alertEl.style.display = 'block';
      alertEl.className = 'alert alert-danger';
      alertEl.textContent = error.message || 'Could not send notification.';
    }
  }
}

async function loadScheduledNotices() {
  const body = document.getElementById('scheduled-table');
  if (!body) return;
  try {
    const rows = asList(await apiGet('/notification/scheduled'));
    body.innerHTML = rows.length
      ? rows.map((r) => {
          const when = r.scheduledForMillis ? new Date(Number(r.scheduledForMillis)).toLocaleString() : '';
          const status = r.cancelled ? 'Cancelled' : (r.released ? 'Sent' : 'Waiting');
          const cancel = !r.released && !r.cancelled
            ? `<button class="btn btn-sm btn-outline-danger" type="button" onclick="cancelScheduledNotice(${r.scheduleId})">Cancel</button>`
            : '';
          return `<tr>
            <td><strong>${r.title || ''}</strong><div class="text-secondary">${r.message || ''}</div></td>
            <td>${when}</td>
            <td>${r.audience || ''}</td>
            <td>${status}</td>
            <td>${cancel}</td>
          </tr>`;
        }).join('')
      : '<tr><td colspan="5" class="text-muted">No scheduled notices. Run docs/sql/create_scheduled_notification.sql if this list fails to load.</td></tr>';
  } catch (error) {
    body.innerHTML = `<tr><td colspan="5" class="text-danger">${error.message || 'Could not load scheduled notices.'}</td></tr>`;
  }
}

async function cancelScheduledNotice(id) {
  try {
    await apiPost('/notification/scheduled/' + id + '/cancel', {});
    await loadScheduledNotices();
  } catch (error) {
    alert(error.message || 'Could not cancel that notice.');
  }
}

window.cancelScheduledNotice = cancelScheduledNotice;

window.sendBroadcast = sendBroadcast;
document.addEventListener('DOMContentLoaded', () => {
  loadNotificationsPage();
  loadScheduledNotices();
});
