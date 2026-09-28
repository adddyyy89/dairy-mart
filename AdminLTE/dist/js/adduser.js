function currentUser() {
  try { return JSON.parse(sessionStorage.getItem('user') || '{}'); } catch (_) { return {}; }
}

function queryTypeId() {
  const type = parseInt(new URLSearchParams(window.location.search).get('type') || '', 10);
  return type === 1 || type === 2 || type === 3 ? type : null;
}

function redirectForType(typeId) {
  if (typeId === 2) return './salesmen.html';
  if (typeId === 3) return './retailers.html';
  return './users.html';
}

function applyRoleLock() {
  const typeId = queryTypeId();
  const typeSelect = document.getElementById('userTypeId');
  const shopGroup = document.getElementById('shopNameGroup');
  const shopInput = document.getElementById('shopName');
  const title = document.getElementById('pageTitle');
  const cancel = document.getElementById('cancelLink');
  const submit = document.querySelector('#addUserForm button[type="submit"]');

  const toggleShop = (id) => {
    const show = Number(id) === 3;
    shopGroup.style.display = show ? '' : 'none';
    shopInput.required = show;
    if (!show) shopInput.value = '';
  };

  if (typeId) {
    typeSelect.value = String(typeId);
    typeSelect.setAttribute('disabled', 'disabled');
    if (title) {
      title.textContent = typeId === 2 ? 'Add Salesman' : typeId === 3 ? 'Add Retailer' : 'Add Admin';
    }
    if (submit) {
      submit.innerHTML = typeId === 2
        ? '<i class="bi bi-plus-lg me-1"></i>Add Salesman'
        : typeId === 3
          ? '<i class="bi bi-plus-lg me-1"></i>Add Retailer'
          : submit.innerHTML;
    }
    if (cancel) cancel.href = redirectForType(typeId);
  }

  toggleShop(typeSelect.value);
  typeSelect.addEventListener('change', () => toggleShop(typeSelect.value));
}

async function populateStates() {
  const dropdown = document.getElementById('stateDropdown');
  try {
    const data = await apiGet('/address/state/get/all');
    const states = asList(data);
    dropdown.innerHTML = '<option value="">-- Select a State --</option>';
    states.forEach((state) => {
      const option = document.createElement('option');
      option.value = state.stateId;
      option.textContent = state.stateName;
      dropdown.appendChild(option);
    });
  } catch (error) {
    console.error('Error fetching states:', error);
    dropdown.innerHTML = '<option value="">Error loading states</option>';
  }
}

async function fetchCities() {
  const stateId = document.getElementById('stateDropdown').value;
  const cityDropdown = document.getElementById('cityDropdown');
  if (!stateId) {
    cityDropdown.innerHTML = '<option value="">-- Select a City --</option>';
    cityDropdown.disabled = true;
    return;
  }
  try {
    const data = await apiGet('/address/city/getbystate/' + stateId);
    const cities = asList(data);
    cityDropdown.innerHTML = '<option value="">-- Select a City --</option>';
    cityDropdown.disabled = false;
    cities.forEach((city) => {
      const option = document.createElement('option');
      option.value = city.cityId;
      option.textContent = city.cityName;
      cityDropdown.appendChild(option);
    });
  } catch (error) {
    console.error('Error:', error);
    cityDropdown.innerHTML = '<option value="">Error loading cities</option>';
  }
}

document.addEventListener('DOMContentLoaded', () => {
  applyRoleLock();
  populateStates();

  document.getElementById('addUserForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const alertDiv = document.getElementById('alertMessage');
    const typeSelect = document.getElementById('userTypeId');
    const userTypeId = parseInt(typeSelect.value, 10);
    const sessionUser = currentUser();

    const phoneNumber = document.getElementById('phoneNumber').value.replace(/\D/g, '');
    if (!/^\d{10}$/.test(phoneNumber)) {
      alertDiv.className = 'alert alert-danger mt-3';
      alertDiv.innerHTML = '<i class="bi bi-exclamation-triangle me-2"></i>Phone number must be 10 digits.';
      alertDiv.style.display = 'block';
      return;
    }

    const newUserData = {
      firstName: document.getElementById('firstName').value,
      lastName: document.getElementById('lastName').value || '',
      phoneNumber,
      userTypeId,
      emailId: document.getElementById('emailId').value,
      password: document.getElementById('password').value,
      isActive: document.getElementById('isActive').checked,
      createdBy: sessionUser.userId || 1,
      address: {
        fullAddress: document.getElementById('fullAddress').value,
        cityId: parseInt(document.getElementById('cityDropdown').value, 10) || 0,
      },
    };

    try {
      const created = await apiPost('/user/add', newUserData);
      const userId = created.userId || created.map?.userId;
      let extra = '';

      if (userTypeId === 3) {
        const shopName = document.getElementById('shopName').value.trim();
        if (shopName && userId) {
          try {
            await apiPost('/shop/add', {
              shopName,
              userId,
              addressId: created.addressId || created.address?.addressId || 0,
              isActive: true,
              createdBy: sessionUser.userId || 1,
              gst: {
                gstNumber: document.getElementById('gstNumber').value.trim(),
                panNumber: document.getElementById('panNumber').value.trim(),
                aadharNumber: document.getElementById('aadharNumber').value.trim(),
              },
            });
          } catch (shopErr) {
            extra = ' User was created, but shop could not be added: ' + shopErr.message;
          }
        }
      }

      alertDiv.className = extra ? 'alert alert-warning mt-3' : 'alert alert-success mt-3';
      alertDiv.innerHTML = extra
        ? '<i class="bi bi-exclamation-triangle me-2"></i>' + extra
        : '<i class="bi bi-check-circle me-2"></i>User added successfully!';
      alertDiv.style.display = 'block';
      document.getElementById('addUserForm').reset();
      typeSelect.value = String(userTypeId);
      setTimeout(() => {
        window.location.href = redirectForType(queryTypeId() || userTypeId);
      }, 1500);
    } catch (error) {
      alertDiv.className = 'alert alert-danger mt-3';
      alertDiv.innerHTML = '<i class="bi bi-exclamation-triangle me-2"></i>' + error.message;
      alertDiv.style.display = 'block';
    }
  });
});
