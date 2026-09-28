const API_BASE = localStorage.getItem('dairymartApiBase') || 'http://localhost:8080';

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
  const user = JSON.parse(sessionStorage.getItem('user') || '{}');
  const headers = { 'Content-Type': 'application/json' };
  if (user.phoneNumber && user.password) {
    headers.Authorization = 'Basic ' + btoa(`${user.phoneNumber}:${user.password}`);
  }
  return headers;
}

async function apiGet(path) {
  const res = await fetch(API_BASE + path, { headers: authHeaders() });
  const text = await res.text();
  let parsed = null;
  try { parsed = text ? JSON.parse(text) : null; } catch (_) { parsed = text; }
  if (!res.ok) {
    const msg = (parsed && parsed.message) || (typeof parsed === 'string' ? parsed : `Request failed (${res.status})`);
    throw new Error(msg);
  }
  return unwrap(parsed);
}

async function apiPost(path, body) {
  const res = await fetch(API_BASE + path, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body),
  });
  const text = await res.text();
  let parsed = null;
  try { parsed = text ? JSON.parse(text) : null; } catch (_) { parsed = text; }
  if (!res.ok) {
    const msg = (parsed && parsed.message) || (typeof parsed === 'string' ? parsed : `Request failed (${res.status})`);
    throw new Error(msg);
  }
  return unwrap(parsed);
}
