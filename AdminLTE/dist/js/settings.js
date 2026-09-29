function fillSettingsForm() {
  const s = dmSettings();
  document.getElementById('apiBase').value = s.apiBase || '';
  document.getElementById('defaultBranchId').value = s.defaultBranchId || 7;
  document.getElementById('pageRefreshEnabled').checked = s.pageRefreshEnabled !== false;
  document.getElementById('pageRefreshSeconds').value = s.pageRefreshSeconds || 60;
  document.getElementById('liveMapSeconds').value = s.liveMapSeconds || 30;
  document.getElementById('headerNotifSeconds').value = s.headerNotifSeconds || 60;
  document.getElementById('gpsStaleMinutes').value = s.gpsStaleMinutes || 15;
}

function saveAdminSettings() {
  const alertEl = document.getElementById('alertMessage');
  const apiBase = (document.getElementById('apiBase').value || '').trim();
  if (!apiBase) {
    alertEl.style.display = 'block';
    alertEl.className = 'alert alert-danger';
    alertEl.textContent = 'API server URL is required.';
    return;
  }
  const pageRefreshSeconds = Number(document.getElementById('pageRefreshSeconds').value || 0);
  if (document.getElementById('pageRefreshEnabled').checked && pageRefreshSeconds < 15) {
    alertEl.style.display = 'block';
    alertEl.className = 'alert alert-danger';
    alertEl.textContent = 'Page refresh must be at least 15 seconds.';
    return;
  }
  dmSaveSettings({
    apiBase,
    defaultBranchId: Number(document.getElementById('defaultBranchId').value || 7),
    pageRefreshEnabled: document.getElementById('pageRefreshEnabled').checked,
    pageRefreshSeconds,
    liveMapSeconds: Number(document.getElementById('liveMapSeconds').value || 30),
    headerNotifSeconds: Number(document.getElementById('headerNotifSeconds').value || 60),
    gpsStaleMinutes: Number(document.getElementById('gpsStaleMinutes').value || 15),
  });
  alertEl.style.display = 'block';
  alertEl.className = 'alert alert-success';
  alertEl.textContent = 'Settings saved. Other open pages pick them up on the next refresh.';
}

document.addEventListener('DOMContentLoaded', fillSettingsForm);
