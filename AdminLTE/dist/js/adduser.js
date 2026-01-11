async function populateStates() {
  const dropdown = document.getElementById('stateDropdown');
  const apiUrl = 'http://localhost:8080/address/state/get/all';

  try {
    const response = await fetch(apiUrl);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const states = await response.json();

    // Clear existing options and add a default prompt
    dropdown.innerHTML = '<option value="">-- Select a State --</option>';

    // Map through the JSON data
    states.forEach(state => {
      const option = document.createElement('option');
      option.value = state.stateId;   // Using stateId as the value
      option.textContent = state.stateName; // Display name
      dropdown.appendChild(option);
    });

  } catch (error) {
    console.error('Error fetching states:', error);
    dropdown.innerHTML = '<option value="">Error loading states</option>';
  }
}

// Call the function when the page loads
window.onload = populateStates;

async function fetchCities() {
  const stateId = document.getElementById('stateDropdown').value;
  const cityDropdown = document.getElementById('cityDropdown');

  // If no state is selected, reset and disable city dropdown
  if (!stateId) {
    cityDropdown.innerHTML = '<option value="">-- Select a City --</option>';
    cityDropdown.disabled = true;
    return;
  }

  try {

    const response = await fetch('http://localhost:8080/address/city/getbystate/' + stateId);

    if (!response.ok) throw new Error('Failed to fetch cities');

    const cities = await response.json();

    // Clear and enable the dropdown
    cityDropdown.innerHTML = '<option value="">-- Select a City --</option>';
    cityDropdown.disabled = false;

    // Populate with new data
    cities.forEach(city => {
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

document.getElementById('addUserForm').addEventListener('submit', async function (e) {
  e.preventDefault();

  const alertDiv = document.getElementById('alertMessage');

  const newUserData = {
    firstName: document.getElementById('firstName').value,
    lastName: document.getElementById('lastName').value || '',
    phoneNumber: document.getElementById('phoneNumber').value,
    userTypeId: parseInt(document.getElementById('userTypeId').value),
    emailId: document.getElementById('emailId').value,
    password: document.getElementById('password').value,
    isActive: document.getElementById('isActive').checked,
    address: {
      fullAddress: document.getElementById('fullAddress').value,
      cityId: parseInt(document.getElementById('cityDropdown').value) || 0
    }
  };

  try {
    const sessionString = sessionStorage.getItem('user');
    const userData = JSON.parse(sessionString);
    const username = userData.phoneNumber;
    const password = userData.password;
    const encodedCredentials = btoa(`${username}:${password}`);
    const response = await fetch('http://localhost:8080/user/add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        // Add the Authorization header here
        'Authorization': `Basic ${encodedCredentials}`
      },
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newUserData)
    });

    if (response.ok) {
      alertDiv.className = 'alert alert-success mt-3';
      alertDiv.innerHTML = '<i class="bi bi-check-circle me-2"></i>User added successfully!';
      alertDiv.style.display = 'block';

      // Reset form
      document.getElementById('addUserForm').reset();

      // Redirect after 2 seconds
      setTimeout(() => {
        window.location.href = './users.html';
      }, 2000);
    } else {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to add user');
    }
  } catch (error) {
    alertDiv.className = 'alert alert-danger mt-3';
    alertDiv.innerHTML = '<i class="bi bi-exclamation-triangle me-2"></i>' + error.message;
    alertDiv.style.display = 'block';
  }
});