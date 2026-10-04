function restrictPhoneInput(input) {
  if (!input) return;
  input.setAttribute('maxlength', '10');
  input.setAttribute('inputmode', 'numeric');
  input.setAttribute('pattern', '[0-9]{10}');
  input.addEventListener('input', () => {
    input.value = String(input.value || '').replace(/\D/g, '').slice(0, 10);
  });
}

function parseUserId(value) {
  if (value === undefined || value === null || value === '') return null;
  const id = Number(value);
  return Number.isFinite(id) ? id : null;
}

function finishLogin(sessionUser) {
  localStorage.setItem('user', JSON.stringify(sessionUser));
  sessionStorage.setItem('isLoggedIn', 'true');
  sessionStorage.setItem('user', JSON.stringify(sessionUser));
  window.location.href = './home.html';
}

document.addEventListener('DOMContentLoaded', () => {
  restrictPhoneInput(document.getElementById('phoneNumber'));
});

document.getElementById('loginForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  const alertDiv = document.getElementById('alertMessage');
  const loginData = {
    phoneNumber: document.getElementById('phoneNumber').value.replace(/\D/g, ''),
    password: document.getElementById('loginPassword').value,
  };

  if (!/^\d{10}$/.test(loginData.phoneNumber)) {
    alertDiv.className = 'alert alert-danger mt-3';
    alertDiv.innerHTML = '<i class="bi bi-exclamation-triangle me-2"></i> Phone number must be 10 digits.';
    alertDiv.style.display = 'block';
    return;
  }

  try {
    const responseData = unwrap(await apiPost('/auth/login', loginData));
    const userId = parseUserId(responseData.userId ?? responseData.userid);
    if (userId == null) {
      alertDiv.className = 'alert alert-danger mt-3';
      alertDiv.innerHTML = '<i class="bi bi-exclamation-triangle me-2"></i> Login succeeded but no user id was returned.';
      alertDiv.style.display = 'block';
      return;
    }
    finishLogin({
      phoneNumber: loginData.phoneNumber,
      password: loginData.password,
      userId: userId,
      role: Number(responseData.role ?? 0),
      isActive: true,
      name: responseData.name || 'Admin',
    });
  } catch (error) {
    alertDiv.className = 'alert alert-danger mt-3';
    alertDiv.innerHTML = '<i class="bi bi-exclamation-triangle me-2"></i>'
      + (error.message || SERVER_DOWN_MESSAGE);
    alertDiv.style.display = 'block';
  }
});
