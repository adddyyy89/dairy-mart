

document.addEventListener('DOMContentLoaded', function() {

    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');

    try {
        const sessionString = sessionStorage.getItem('user');
        const userData = JSON.parse(sessionString);
        const username = userData.phoneNumber;
        const password = userData.password;
        const encodedCredentials = btoa(`${username}:${password}`);
        const response = fetch(`http://localhost:8080/retailorder/get/${id}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                // Add the Authorization header here
                'Authorization': `Basic ${encodedCredentials}`
            }
        }).then(response => {
            if (response.status === 401) throw new Error('Unauthorized: Invalid credentials');
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            renderOrderData(data);
        })
        .catch(error => {
            console.error('Error fetching order:', error);
            document.querySelector('.order-card').innerHTML = 
                `<div class="alert alert-danger">
                    <strong>Error:</strong> ${error.message}. <br>
                    Make sure your credentials are correct and CORS is enabled on the server.
                </div>`;
        });

    } catch (error) {
        console.error("Error:", error);
        alert("Could not load user details.");
    }

    // dummy data for now
    //originalData = dummyUserData;
    //populateForm(dummyUserData);
});


function renderOrderData(data) {
    // Helper function to update text safely
    const updateText = (id, val) => {
        const el = document.getElementById(id);
        if (el) el.textContent = val;
    };
    
    // Map top-level details
    document.getElementById('orderId').textContent = data.orderId;
    document.getElementById('orderDate').textContent = data.orderDate;
    document.getElementById('statusDesc').innerHTML = `<span class="badge bg-success">${data.status.statusDesc}</span>`;
    document.getElementById('branchName').textContent = data.branch.branchName;
    document.getElementById('retailerName').textContent = data.retailer.shopName;
    document.getElementById('retailerAddress').textContent = data.retailer.address.fullAddress;

    // Map Table Rows
    const tableBody = document.getElementById('orderItemsTable');
    tableBody.innerHTML = ''; // Clear loader if any

    data.orderDetails.forEach(item => {
        const row = `
                <tr>
                    <td><strong>${item.productCode}</strong></td>
                    <td>${item.quantity}</td>
                    <td>${item.unit}</td>
                    <td>₹${item.saleRate}</td>
                    <td>₹${item.purchaseRate}</td>
                </tr>
            `;
        tableBody.innerHTML += row;
    });
}
