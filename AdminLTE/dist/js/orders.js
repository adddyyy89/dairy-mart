// Filter by User Type Button
document.addEventListener('DOMContentLoaded', () => {
    const rows = document.querySelectorAll('.clickable-row');
    rows.forEach(row => {
        row.addEventListener('click', () => {
            window.location.href = "orderdetail" + row.dataset.href;
        });
    });
});

function filterTable(type) {
    const table = document.getElementById("orderTable");
    const tr = table.getElementsByTagName("tr");
    const today = new Date().toISOString().split('T')[0];

    for (let i = 1; i < tr.length; i++) {
        const typeColumn = tr[i].getElementsByTagName("td")[1]; // Order Date is 2nd column
        if (typeColumn) {
            const textValue = typeColumn.textContent || typeColumn.innerText;
            if (type === "Show All" || textValue.trim() === today) {
                tr[i].style.display = "";
            } else {
                tr[i].style.display = "none";
            }
        }
    }
}

// Dynamic Search Input
function searchTable() {
    const input = document.getElementById("searchInput");
    const filter = input.value.toUpperCase();
    const table = document.getElementById("orderTable");
    const tr = table.getElementsByTagName("tr");

    for (let i = 1; i < tr.length; i++) {
        let found = false;
        const tds = tr[i].getElementsByTagName("td");
        for (let j = 0; j < tds.length; j++) {
            if (tds[j].textContent.toUpperCase().indexOf(filter) > -1) {
                found = true;
            }
        }
        tr[i].style.display = found ? "" : "none";
    }
}

async function loadOrders() {

    try {

        const sessionString = sessionStorage.getItem('user');
        const userData = JSON.parse(sessionString);
        const username = userData.phoneNumber;
        const password = userData.password;
        const encodedCredentials = btoa(`${username}:${password}`);

        // Fetch retailer-salesman map
        const retailSalesResponse = await fetch('http://localhost:8080/salesmantoretail/get/all', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Basic ${encodedCredentials}`
            }
        });

        if (!retailSalesResponse.ok) throw new Error("Failed to fetch orders");

        const retailSales = await retailSalesResponse.json();

        const salesmanRetailerMap = {};

        retailSales.forEach(item => {
            // We use the Salesman ID as the key because object keys must be strings/symbols
            const key = item.salesmanId;
            salesmanRetailerMap[key] = {
                salesman: item.salesman,
                retailer: item.retailer
            };
        });

        sessionStorage.setItem('salesmanRetailerDataMap', JSON.stringify(salesmanRetailerMap));

        // Create a retailer salesman map
        const retailerSalesmanMap = {};

        retailSales.forEach(item => {
            // retailerId is the unique key (e.g., 201, 401, 601)
            const key = item.retailerId;

            // The value is the nested salesman object
            retailerSalesmanMap[key] = item.salesman;
        });

        sessionStorage.setItem('retailerSalesmanDataMap', JSON.stringify(retailerSalesmanMap));


        // Fetch all orders
        const response = await fetch('http://localhost:8080/retailorder/get/all', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Basic ${encodedCredentials}`
            }
        });

        if (!response.ok) throw new Error("Failed to fetch orders");

        // The response is a direct array [], so we don't need .map or .myArrayList
        const orders = await response.json();


        const tbody = document.getElementById("orderTable");
        tbody.innerHTML = "";

        orders.forEach(order => {
            // Map JSON keys to your table columns
            const row = `
        <tr class="clickable-row" onclick="goToDetails(${order.orderId})">
          <td>${order.orderId}</td>
          <td>${order.orderDate}</td>
          <td class="fw-bold">${order.retailer.shopId}</td>
          <td>${order.retailer.shopName}</td>
          <td>${order.branch.branchName}</td>
          <td>${order.createdBy}</td>
          <td>${order.createdon}</td>
          <td><span class="badge bg-info text-dark">${order.status.statusDesc}</span></td>
          <td>${retailerSalesmanMap[order.retailerId].firstName}</td>
        </tr>
      `;
            tbody.innerHTML += row;
        });
    } catch (error) {
        console.error("Error fetching products:", error);
        document.getElementById("product-table-body").innerHTML =
            `<tr><td colspan="7" class="text-center text-danger">Failed to load products.</td></tr>`;
    }
}

function goToDetails(orderId) {
    window.location.href = `orderdetails.html?id=${orderId}`;
}

loadOrders();