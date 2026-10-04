let originalData = {};

let originalShop = null;

function displayTax(value) {
  if (!value || value === 'NA') return '';
  return value;
}

function userTypeIdOf(data) {
  return Number(data.userTypeId || data.type?.userTypeId || 0);
}

async function loadShopForUser(userId, typeId) {
  const section = document.getElementById('retailerGstSection');
  originalShop = null;
  if (Number(typeId) !== 3) {
    section.style.display = 'none';
    return;
  }
  try {
    const shops = asList(await apiGet('/shop/get/user/' + userId));
    const shop = shops[0] || null;
    originalShop = shop;
    if (!shop) {
      section.style.display = 'none';
      return;
    }
    section.style.display = '';
    const gst = shop.gst || {};
    document.getElementById('shopId').value = shop.shopId ?? '';
    document.getElementById('shopName').value = shop.shopName ?? '';
    document.getElementById('gstNumber').value = displayTax(gst.gstNumber || shop.gstNumber);
    document.getElementById('panNumber').value = displayTax(gst.panNumber || shop.panNumber);
    document.getElementById('aadharNumber').value = displayTax(gst.aadharNumber || shop.aadharNumber);
  } catch (error) {
    console.error(error);
    section.style.display = 'none';
  }
}

async function loadUserData() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');

    try {
        const data = await apiGet('/user/get/' + id);
        originalData = data;
        populateForm(data);
        await loadShopForUser(data.userId, userTypeIdOf(data));
    } catch (error) {
        console.error("Error:", error);
        alert("Could not load user details.");
    }
}


function populateForm(data) {
    // 1. Fill top-level fields
    document.getElementById('userId').value = data.userId ?? "";
    document.getElementById('userFirstName').value = data.firstName ?? "";
    document.getElementById('userLastName').value = data.lastName ?? "";
    document.getElementById('userEmail').value = data.emailId ?? "";
    document.getElementById('phoneNumber').value = data.phoneNumber ?? "";

    // 2. User Type
    if (data.type) {
        document.getElementById('userTypeId').value = data.type.userTypeId ?? "";
    }
    if(data.isActive === false || data.active === false) {
        document.getElementById('isActive').checked = false;
    } else {
        document.getElementById('isActive').checked = true;
    }
     

    // 3. Address Field
    if (data.address) {
        document.getElementById('fullAddress').value = data.address.fullAddress ?? "";

        if (data.address.city) {
            document.getElementById('city').value = data.address.city.cityName ?? "";

            if (data.address.city.state) {
                document.getElementById('state').value = data.address.city.state.stateName ?? "";

                if (data.address.city.state.country) {
                    document.getElementById('country').value = data.address.city.state.country.countryName ?? "";
                }
            }
        }
    }
}

// TOGGLE EDIT MODE (Same as previous step)
function toggleEditMode() {
    const inputs = document.querySelectorAll('.editable-field');
    // We don't want to edit IDs or internal names usually, but for this demo:
    inputs.forEach(input => {
        // List of IDs that should remain ReadOnly
        const protectedIds = ['userId', 'fullAddress', 'city', 'state', 'country'];

        if (!protectedIds.includes(input.id)) {
            input.readOnly = false;
            input.classList.add('border');
        }

        if (input.id === 'userTypeId') {
            input.disabled = false;
        }
    });
    document.getElementById('editBtn').classList.add('d-none');
    document.getElementById('actionButtons').classList.remove('d-none');
}

function cancelEdit() {
    populateForm(originalData);
    if (originalShop) {
      loadShopForUser(originalData.userId, 3);
    }

    // 2. Select all editable fields
    const inputs = document.querySelectorAll('.editable-field');
    const editBtn = document.getElementById('editBtn');
    const actionButtons = document.getElementById('actionButtons');

    // 3. Switch fields back to Read-Only and remove the border
    inputs.forEach(input => {
        input.readOnly = true;
        input.classList.remove('border');
    });

    // 4. Toggle button visibility
    editBtn.classList.remove('d-none');     // Show "Edit" button
    actionButtons.classList.add('d-none');  // Hide "Save/Cancel" buttons

    // 5. Optional: Scroll to the top of the card so the user sees the reset
    document.querySelector('.card').scrollIntoView({ behavior: 'smooth' });
}

// SAVE CHANGES
document.getElementById('productForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = new FormData(e.target);
    const updatedData = Object.fromEntries(formData.entries());

    const phoneNumber = String(updatedData.phoneNumber || document.getElementById('phoneNumber').value || '').replace(/\D/g, '');
    if (!/^\d{10}$/.test(phoneNumber)) {
        alert('Phone number must be 10 digits.');
        return;
    }

    const userPayload = {
        userId: updatedData.userId || originalData.userId,
        firstName: updatedData.firstName || document.getElementById('userFirstName').value,
        lastName: updatedData.lastName || document.getElementById('userLastName').value,
        emailId: updatedData.emailId || document.getElementById('userEmail').value,
        phoneNumber,
        userTypeId: updatedData.userTypeId || userTypeIdOf(originalData),
        addressId: originalData.address?.addressId,
        password: originalData.password,
        isActive: document.getElementById('isActive').checked,
        active: document.getElementById('isActive').checked
    };

    console.log('Updated user data:', userPayload);

    // go back to view mode
    const inputs = document.querySelectorAll('.editable-field');
    inputs.forEach(input => {
        input.readOnly = true;
        input.classList.remove('border');
    });
    document.getElementById('editBtn').classList.remove('d-none');
    document.getElementById('actionButtons').classList.add('d-none');


    try {
        const sessionString = sessionStorage.getItem('user');
        const userData = JSON.parse(sessionString);
        const username = userData.phoneNumber;
        const password = userData.password;
        const encodedCredentials = btoa(`${username}:${password}`);

        const response = await fetch(`http://localhost:8080/user/update`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Basic ${encodedCredentials}`
            },
            body: JSON.stringify(userPayload)
        });

        if (response.ok) {
            const shopId = parseInt(document.getElementById('shopId').value, 10);
            if (shopId) {
                try {
                    await apiPost('/shop/update', {
                        shopId,
                        shopName: document.getElementById('shopName').value.trim(),
                        userId: parseInt(originalData.userId, 10),
                        addressId: originalShop?.addressId || originalData.address?.addressId || 0,
                        gstId: originalShop?.gstId || originalShop?.gst?.gstId || 0,
                        isActive: true,
                        gst: {
                            gstId: originalShop?.gstId || originalShop?.gst?.gstId || 0,
                            gstNumber: document.getElementById('gstNumber').value.trim(),
                            panNumber: document.getElementById('panNumber').value.trim(),
                            aadharNumber: document.getElementById('aadharNumber').value.trim(),
                        },
                    });
                } catch (shopErr) {
                    alert('User saved, but shop GST update failed: ' + shopErr.message);
                    return;
                }
            }
            location.reload();
        } else {
            alert('Update failed.');
        }
    } catch (error) {
        console.error('Error:', error);
    }

});

window.onload = loadUserData;