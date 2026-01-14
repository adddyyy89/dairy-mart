let originalData = {};

let dummyUserData =
{
    "userId": 0,
    "phoneNumber": "9674350488",
    "firstName": "admin",
    "lastName": "user",
    "userTypeId": 0,
    "emailId": "dairymartbussinessmail@gmail.com",
    "addressId": 0,
    "createdBy": 0,
    "createdOn": "2024-10-16",
    "lastUpdated": "2024-10-16",
    "crateCount": 0,
    "type": {
        "userTypeId": 0,
        "userTypeDesc": "admin",
        "isActive": true,
        "createOn": "Oct 16, 2024",
        "createBy": 0
    },
    "address": {
        "addressId": 0,
        "fullAddress": "Sample Test Address",
        "pinCode": "263139",
        "cityId": 0,
        "city": {
            "cityId": 0,
            "cityName": "Haldwani",
            "stateId": 0,
            "state": {
                "stateId": 0,
                "stateName": "Uttarakhand",
                "countryId": 0,
                "country": {
                    "countryId": 0,
                    "countryName": "India"
                }
            }
        }
    },
    "isActive": true,
    "password": "{noop}admin"
}

async function loadUserData() {

    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');

    try {
        const sessionString = sessionStorage.getItem('user');
        const userData = JSON.parse(sessionString);
        const username = userData.phoneNumber;
        const password = userData.password;
        const encodedCredentials = btoa(`${username}:${password}`);
        const response = await fetch(`http://localhost:8080/user/get/${id}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                // Add the Authorization header here
                'Authorization': `Basic ${encodedCredentials}`
            }
        });
        if (!response.ok) throw new Error("User not found");

        const data = await response.json();
        originalData = data;
        populateForm(data);
    } catch (error) {
        console.error("Error:", error);
        alert("Could not load user details.");
    }

    // dummy data for now
    //originalData = dummyUserData;
    //populateForm(dummyUserData);
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
        document.getElementById('userType').value = data.type.userTypeDesc ?? "";
    }

    // 3. Address Field
    if (data.address) {
        document.getElementById('fullAddress').value = data.address.fullAddress ?? "";
        document.getElementById('pinCode').value = data.address.pinCode ?? "";

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
        // Exclude productId from being edited if desired
        if (input.id !== 'userId') {
            input.readOnly = false;
            input.classList.add('border');
        }
    });
    document.getElementById('editBtn').classList.add('d-none');
    document.getElementById('actionButtons').classList.remove('d-none');
}

function cancelEdit() {
    // 1. Re-populate the form with the original data stored during load
    populateForm(originalData);

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

    const userPayload = {
        userId: updatedData.userId,
        firstName: updatedData.firstName,
        lastName: updatedData.lastName,
        emailId: updatedData.emailId,
        phoneNumber: updatedData.phoneNumber,
        address: {
            fullAddress: updatedData.fullAddress,
            pinCode: updatedData.pinCode,
            city: {
                cityName: updatedData.city,
                state: {
                    stateName: updatedData.state,
                    country: {
                        countryName: updatedData.country
                    }
                }
            }
        },
        type: {
            userTypeDesc: updatedData.userType
        }
    };

    console.log('Updated user data:', userPayload);
    alert('User updated successfully!');

    // go back to view mode
    const inputs = document.querySelectorAll('.editable-field');
    inputs.forEach(input => {
        input.readOnly = true;
        input.classList.remove('border');
    });
    document.getElementById('editBtn').classList.remove('d-none');
    document.getElementById('actionButtons').classList.add('d-none');

    /*
    try {
        const response = await fetch('http://localhost:8080/user/update', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userPayload)
        });

        if (response.ok) {
            alert('User updated successfully!');
            location.reload();
        } else {
            alert('Update failed.');
        }
    } catch (error) {
        console.error('Error:', error);
    }
    */
});

window.onload = loadUserData;