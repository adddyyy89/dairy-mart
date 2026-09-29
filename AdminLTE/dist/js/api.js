const DM_SETTING_DEFAULTS = {
  apiBase: 'http://localhost:8080',
  pageRefreshEnabled: true,
  pageRefreshSeconds: 60,
  liveMapSeconds: 30,
  headerNotifSeconds: 60,
  gpsStaleMinutes: 15,
  defaultBranchId: 7,
};

const SERVER_DOWN_MESSAGE = 'The server is down. Please try again later.';

/** Saved AdminLTE options for this browser, merged with defaults. */
function dmSettings() {
  let saved = {};
  try {
    saved = JSON.parse(localStorage.getItem('dairymartSettings') || '{}') || {};
  } catch (_) {
    saved = {};
  }
  const legacyBase = localStorage.getItem('dairymartApiBase');
  const merged = Object.assign({}, DM_SETTING_DEFAULTS, saved);
  if (!saved.apiBase && legacyBase) {
    merged.apiBase = legacyBase;
  }
  return merged;
}

function dmApiBase() {
  return (dmSettings().apiBase || DM_SETTING_DEFAULTS.apiBase).replace(/\/$/, '');
}

function dmSaveSettings(next) {
  const merged = Object.assign({}, dmSettings(), next || {});
  localStorage.setItem('dairymartSettings', JSON.stringify(merged));
  localStorage.setItem('dairymartApiBase', merged.apiBase || '');
  return merged;
}

function httpErrorMessage(status, parsed, text) {
  const fromBody = extractMessage(parsed, text);
  if (fromBody) return fromBody;
  if (status === 400) return 'That request was not valid. Check the details and try again.';
  if (status === 401) return 'Your session expired. Please sign in again.';
  if (status === 403) return 'You are not allowed to do this.';
  if (status === 404) return 'That record was not found.';
  if (status === 0 || status >= 500) return SERVER_DOWN_MESSAGE;
  return 'Request failed (' + status + ')';
}

function extractMessage(parsed, text) {
  const node = unwrap(parsed);
  if (typeof node === 'string') {
    const s = node.trim();
    if (s && !s.startsWith('<')) return s;
  }
  if (node && typeof node === 'object') {
    for (const key of ['message', 'error', 'errorMessage', 'detail']) {
      if (typeof node[key] === 'string' && node[key].trim()) return node[key].trim();
    }
  }
  if (typeof text === 'string') {
    const s = text.trim();
    if (s && !s.startsWith('<') && !s.startsWith('{') && !s.startsWith('[')) return s;
  }
  return '';
}

function unwrap(node) {
  if (Array.isArray(node)) return node.map(unwrap);
  if (node && typeof node === 'object') {
    const keys = Object.keys(node);
    if (keys.length === 1 && keys[0] === 'map') return unwrap(node.map);
    if (keys.length === 1 && keys[0] === 'myArrayList') return unwrap(node.myArrayList);
    const out = {};
    keys.forEach((k) => { out[k] = unwrap(node[k]); });
    return out;
  }
  return node;
}

function formatInr(value) {
  const n = Number(value);
  const v = Number.isFinite(n) ? n : 0;
  return '₹' + v.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function personName(user) {
  if (!user) return '';
  return [user.firstName, user.lastName].filter(Boolean).join(' ').trim();
}

function asList(node) {
  const value = unwrap(node);
  if (Array.isArray(value)) return value;
  if (value && Array.isArray(value.users)) return value.users;
  if (value && typeof value === 'object') {
    if (value.shopId != null || value.userId != null) return [value];
    const numericKeys = Object.keys(value).filter((k) => /^\d+$/.test(k));
    if (numericKeys.length && numericKeys.length === Object.keys(value).length) {
      return numericKeys.sort((a, b) => a - b).map((k) => value[k]);
    }
  }
  return [];
}

function restrictPhoneInput(input) {
  if (!input) return;
  input.setAttribute('maxlength', '10');
  input.setAttribute('inputmode', 'numeric');
  input.setAttribute('pattern', '[0-9]{10}');
  input.addEventListener('input', () => {
    input.value = String(input.value || '').replace(/\D/g, '').slice(0, 10);
  });
  input.addEventListener('paste', (e) => {
    e.preventDefault();
    const text = (e.clipboardData || window.clipboardData).getData('text') || '';
    input.value = text.replace(/\D/g, '').slice(0, 10);
  });
}

document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('#phoneNumber, input[name="phoneNumber"]').forEach(restrictPhoneInput);
});

function authHeaders() {
  let user = {};
  try {
    user = unwrap(JSON.parse(sessionStorage.getItem('user') || '{}') || {});
  } catch (_) {
    user = {};
  }
  const headers = { 'Content-Type': 'application/json' };
  if (user.phoneNumber && user.password) {
    headers.Authorization = 'Basic ' + btoa(`${user.phoneNumber}:${user.password}`);
  }
  return headers;
}

/** Sticky banner when the API host cannot be reached or returns 5xx. */
function showServerDownBanner(down) {
  let el = document.getElementById('server-down-banner');
  if (!el) {
    el = document.createElement('div');
    el.id = 'server-down-banner';
    el.setAttribute('role', 'alert');
    el.className = 'alert alert-danger mb-0 rounded-0 text-center';
    el.style.cssText = 'position:sticky;top:0;z-index:4000;display:none';
    el.textContent = SERVER_DOWN_MESSAGE;
    document.body.insertBefore(el, document.body.firstChild);
  }
  el.style.display = down ? 'block' : 'none';
}

function isServerUnreachable(error) {
  const name = error && error.name;
  const msg = String((error && error.message) || error || '');
  return name === 'AbortError' || name === 'TypeError'
    || /failed to fetch|networkerror|load failed|network request failed/i.test(msg);
}

/** GET/POST against the configured API with a 20s timeout. */
async function apiFetch(path, options) {
  const ctrl = new AbortController();
  const timer = setTimeout(() => ctrl.abort(), 20000);
  try {
    const res = await fetch(dmApiBase() + path, Object.assign({
      headers: authHeaders(),
      signal: ctrl.signal,
    }, options));
    const text = await res.text();
    let parsed = null;
    try { parsed = text ? JSON.parse(text) : null; } catch (_) { parsed = text; }
    if (!res.ok) {
      showServerDownBanner(res.status >= 500);
      throw new Error(httpErrorMessage(res.status, parsed, text));
    }
    showServerDownBanner(false);
    return unwrap(parsed);
  } catch (error) {
    if (error && error.message && !isServerUnreachable(error) && error.name !== 'AbortError') {
      throw error;
    }
    if (isServerUnreachable(error) || (error && error.name === 'AbortError')) {
      showServerDownBanner(true);
      throw new Error(SERVER_DOWN_MESSAGE);
    }
    throw error;
  } finally {
    clearTimeout(timer);
  }
}

async function apiGet(path) {
  return apiFetch(path, { method: 'GET' });
}

async function apiPost(path, body) {
  return apiFetch(path, {
    method: 'POST',
    body: JSON.stringify(body),
  });
}

async function ensureSessionUserId() {
  let id = typeof sessionUserId === 'function' ? sessionUserId() : null;
  if (id != null) return id;
  const user = typeof getUser === 'function' ? getUser() : null;
  if (!user) return null;
  try {
    let list = [];
    try {
      list = asList(await apiGet('/user/get/usertype/1'));
    } catch (_) {
      list = asList(await apiGet('/user/get/all')).filter((u) =>
        Number(u.userTypeId || u.typeId || u.type?.userTypeId) === 1
      );
    }
    const phone = user.phoneNumber;
    const match = list.find((u) => String(u.phoneNumber) === String(phone)) || list[0];
    const nextId = match ? Number(match.userId ?? match.userid) : NaN;
    if (Number.isFinite(nextId)) {
      const next = Object.assign({}, user, {
        userId: nextId,
        name: user.name || [match.firstName, match.lastName].filter(Boolean).join(' ').trim(),
      });
      sessionStorage.setItem('user', JSON.stringify(next));
      return nextId;
    }
  } catch (error) {
    console.warn('Could not resolve admin user id', error);
  }
  return null;
}

window.ensureSessionUserId = ensureSessionUserId;
