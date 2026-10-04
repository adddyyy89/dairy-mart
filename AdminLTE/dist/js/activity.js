async function loadActivity() {
  const body = document.getElementById('activity-body');
  try {
    const rows = asList(await apiGet('/admin/activity'));
    body.innerHTML = rows.length
      ? rows.map((n) => `
          <tr>
            <td>${n.kind || ''}</td>
            <td>${n.title || ''}</td>
            <td>${n.message || ''}</td>
            <td>${String(n.createdOn || '').replace('T', ' ').slice(0, 19)}</td>
          </tr>`).join('')
      : '<tr><td colspan="4" class="text-muted">No activity recorded yet.</td></tr>';
  } catch (error) {
    body.innerHTML = `<tr><td colspan="4" class="text-danger">${error.message || 'Could not load activity.'}</td></tr>`;
  }
}

document.addEventListener('DOMContentLoaded', loadActivity);
