async function getData() {
  const url = "http://localhost:8080/admin/dashboard/get";
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    const json = await response.json();
    console.log(json);
    
    // Example: Putting data into an HTML element
    document.getElementById("total-retailers").innerText = json.map.totalRetailers;
    document.getElementById("total-salesman").innerText = json.map.totalSalesman;
    document.getElementById("total-orders").innerText = json.map.totalTodaysOrder;
    document.getElementById("total-transactions").innerText = json.map.totalTodaysTransactions;
    
    var totalOrders = json.map.totalTodaysOrder;
    var newOrders = 0;
    var confirmedOrders = 0;
    var rejectedOrders = 0;
    var dispatchedOrders = 0;
    var deliveredOrders = 0;
    
    for(const order of json.map.latestOrders.myArrayList) {
        if(order.map.status.map.statusDesc === 'NEW') {
            newOrders++;
        }
        if(order.map.status.map.statusDesc === 'CONFIRMED') {
            confirmedOrders++;
        }
        if(order.map.status.map.statusDesc === 'REJECTED') {
            rejectedOrders++;
        }
        if(order.map.status.map.statusDesc === 'DISPATCHED') {
            dispatchedOrders++;
        }
        if(order.map.status.map.statusDesc === 'DELIVERED') {
            deliveredOrders++;
        }
    }

    document.getElementById("order-new").innerHTML = "<b>" + newOrders + "</b>" + "/" + totalOrders;
    document.getElementById("order-confirmed").innerHTML = "<b>" + confirmedOrders + "</b>" + "/" + totalOrders;
    document.getElementById("order-rejected").innerHTML = "<b>" + rejectedOrders + "</b>" + "/" + totalOrders;
    document.getElementById("order-dispatched").innerHTML = "<b>" + dispatchedOrders + "</b>" + "/" + totalOrders;
    document.getElementById("order-delivered").innerHTML = "<b>" + deliveredOrders + "</b>" + "/" + totalOrders;


    // Latest Orders

    const tableBody = document.getElementById("latest-orders-body");
    tableBody.innerHTML = "";

    json.map.latestOrders.myArrayList.forEach(order => {
      const row = `
        <tr>
          <td>
            <a href="pages/examples/invoice.html?id=${order.map.orderId}" 
               class="link-primary link-offset-2 link-underline-opacity-25 link-underline-opacity-100-hover">
               ${order.map.orderId}
            </a>
          </td>
          <td>${order.map.retailer.map.shopName}</td>
          <td><span class="badge text-bg-info">${order.map.status.map.statusDesc}</span></td>
        </tr>
      `;
      
      // Append the row to the table
      tableBody.innerHTML += row;
    });
    
  } catch (error) {
    console.error(error.message);
  }
}

// Call the function
getData();