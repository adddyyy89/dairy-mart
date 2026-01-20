let retailers = [];
let salesmans = [];

document.addEventListener('DOMContentLoaded', async () => {
    const sessionString = sessionStorage.getItem('user');
    if (!sessionString) return;

    const userData = JSON.parse(sessionString);
    const encodedCredentials = btoa(`${userData.phoneNumber}:${userData.password}`);

    // Use Promise.all to fetch both lists in parallel and WAIT for them
    try {
        const [retailerData, salesmanData] = await Promise.all([
            getUsersByType(3, encodedCredentials),
            getUsersByType(2, encodedCredentials)
        ]);

        retailers = retailerData;
        salesmans = salesmanData;

        // Only populate the page AFTER data is received
        populatePageFromSession();
    } catch (error) {
        console.error("Initialization failed:", error);
    }
});

// Added 'async' and 'await' here
async function getUsersByType(userType, encodedCredentials) {
    try {
        const response = await fetch('http://localhost:8080/user/get/usertype/' + userType, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Basic ${encodedCredentials}`
            }
        });

        if (response.status === 401) throw new Error('Unauthorized');
        if (!response.ok) throw new Error('Network response was not ok');

        return await response.json(); // This actually returns the data to the caller
    } catch (error) {
        console.error(`Error fetching user type ${userType}:`, error);
        return []; // Return empty array on error to prevent .forEach crashes
    }
}

function populatePageFromSession() {
    const rawData = sessionStorage.getItem('salesmanRetailerDataMap');
    if (!rawData) {
        console.warn("No mapping data found in sessionStorage.");
        return;
    }

    try {
        const mappings = JSON.parse(rawData);
        const salesmanDropdown = document.getElementById('salesmanSelect');
        const retailerDropdown = document.getElementById('retailerSelect');
        const tableBody = document.getElementById('mappingTableBody');

        tableBody.innerHTML = '';
        salesmanDropdown.innerHTML = '<option value="" selected disabled>Choose a salesman...</option>';
        retailerDropdown.innerHTML = '<option value="" selected disabled>Choose a retailer...</option>';

        // 1. Populate Table
        Object.keys(mappings).forEach(key => {
            const { salesman, retailer } = mappings[key];
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>
                    <div class="fw-bold">${salesman.firstName} ${salesman.lastName}</div>
                    <small class="text-muted">${salesman.phoneNumber}</small>
                </td>
                <td>
                    <div class="fw-bold">${retailer.shopName}</div>
                    <small class="text-muted">${retailer.address.fullAddress}</small>
                </td>
                <td>
                    <span class="badge ${retailer.isActive ? 'bg-success' : 'bg-secondary'}">
                        ${retailer.isActive ? 'Active' : 'Inactive'}
                    </span>
                </td>
                <td class="text-center">
                    <button class="btn btn-sm btn-outline-primary" onclick="editMapping('${key}')"><i class="bi bi-pencil-square"></i></button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteMapping('${key}')"><i class="bi bi-trash"></i></button>
                </td>`;
            tableBody.appendChild(row);
        });

        // 2. Populate Dropdowns (Now retailers and salesmans will have data)
        retailers.forEach(item => {
            if (item.isActive) {
                const opt = new Option(`${item.firstName} ${item.lastName}`.trim(), item.userId);
                retailerDropdown.add(opt);
            }

        });

        salesmans.forEach(item => {
            if (item.isActive) {
                const opt = new Option(`${item.firstName} ${item.lastName}`.trim(), item.userId);
                salesmanDropdown.add(opt);
            }

        });

    } catch (error) {
        console.error("Error populating page:", error);
    }
}

document.getElementById('mappingForm').addEventListener('submit', async (e) => {
    e.preventDefault(); // Prevent page refresh

    const salesmanId = document.getElementById('salesmanSelect').value;
    const retailerId = document.getElementById('retailerSelect').value;
    
    if (!salesmanId || !retailerId ) {
        alert("Please ensure Salesman, Retailer are provided.");
        return;
    }

    const requestBody = {
        "salesmanId": parseInt(salesmanId),
        "retailerId": parseInt(retailerId),
        "vehicleNumber": '',
        "createdBy": 0,
        "active": true,
        "branchId": 0
    };

    await assignMapping(requestBody);
});

async function assignMapping(data) {
    const sessionString = sessionStorage.getItem('user');
    const userData = JSON.parse(sessionString);
    const encodedCredentials = btoa(`${userData.phoneNumber}:${userData.password}`);

    try {
        const response = await fetch('http://localhost:8080/salesmantoretail/assign', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Basic ${encodedCredentials}`
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            alert("Mapping assigned successfully!");
            
            // Optionally refresh the page or update the table/session storage here
            location.reload(); 
        } else {
            const errorData = await response.text();
            alert("Failed to assign mapping: " + errorData);
        }
    } catch (error) {
        console.error('Error during assignment:', error);
        alert("An error occurred while connecting to the server.");
    }
}