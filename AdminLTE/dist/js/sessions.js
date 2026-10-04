function formatWhen(value) {
  if (!value) return '—';
  return String(value).replace('T', ' ').slice(0, 19);
}

async function loadSessions() {
  const onlineBody = document.getElementById('online-body');
  const historyBody = document.getElementById('history-body');
  try {
    const data = unwrap(await apiGet('/admin/sessions'));
    const online = asList(data.online);
    const history = asList(data.history);
    document.getElementById('online-count').textContent = data.onlineCount ?? online.length;
    onlineBody.innerHTML = online.length
      ? online.map((u) => `
          <tr>
            <td>${u.name || ''}</td>
            <td>${u.phoneNumber || ''}</td>
            <td>${u.roleLabel || u.role || ''}</td>
            <td>${formatWhen(u.loggedIn)}</td>
          </tr>`).join('')
      : '<tr><td colspan="4" class="text-muted">Nobody is online.</td></tr>';
    historyBody.innerHTML = history.length
      ? history.map((u) => `
          <tr>
            <td>${u.name || ''}</td>
            <td>${u.phoneNumber || ''}</td>
            <td>${u.roleLabel || u.role || ''}</td>
            <td>${formatWhen(u.loggedIn)}</td>
            <td>${formatWhen(u.loggedOut)}</td>
            <td>${u.active ? '<span class="badge text-bg-success">Online</span>' : '<span class="badge text-bg-secondary">Offline</span>'}</td>
          </tr>`).join('')
      : '<tr><td colspan="6" class="text-muted">No login history yet.</td></tr>';
  } catch (error) {
    onlineBody.innerHTML = `<tr><td colspan="4" class="text-danger">${error.message || 'Could not load sessions.'}</td></tr>`;
    historyBody.innerHTML = '';
  }
}

document.addEventListener('DOMContentLoaded', loadSessions);
