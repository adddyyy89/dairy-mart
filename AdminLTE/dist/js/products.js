// Filter by User Type Button
document.addEventListener('DOMContentLoaded', () => {
    const rows = document.querySelectorAll('.clickable-row');
    rows.forEach(row => {
        row.addEventListener('click', () => {
            window.location.href = "Product" + row.dataset.href;
        });
    });
});


// Dynamic Search Input
function searchTable() {
    const input = document.getElementById("searchInput");
    const filter = input.value.toUpperCase();
    const table = document.getElementById("productTable");
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

async function loadProductTypeData() {

    try {
        const response = await fetch(`http://localhost:8080/product/producttype/getall`);
        if (!response.ok) throw new Error("Product types not found");
        
        const data = await response.json();
        productTypes = data; 

        const productTypeField = document.getElementById("productTypeId");
        productTypeField.innerHTML = '<option value="" selected disabled>Select Product Type</option>';

        productTypes.forEach(productType => {
            // Map JSON keys to your table columns
            console.log(productType);
            const row = `
                <option value="">${productType.productTypeName}</option>
            `;
            productTypeField.innerHTML += row;
        });

    } catch (error) {
        console.error("Error:", error);
        alert("Could not load product details.");
    }
}

async function loadProducts() {
  const url = "http://localhost:8080/product/getall"; 
  try {
    const response = await fetch(url);
    if (!response.ok) throw new Error("Failed to fetch products");

    // The response is a direct array [], so we don't need .map or .myArrayList
    const products = await response.json();
    
    const tbody = document.getElementById("product-table-body");
    tbody.innerHTML = "";

    products.forEach(product => {
      // Map JSON keys to your table columns
      const row = `
        <tr style="cursor: pointer;" onclick="goToDetails(${product.productId})">
          <td>${product.productId}</td>
          <td>
            <img src="${product.productPictureUrl}" 
                 alt="${product.productName}" 
                 style="width: 50px; height: 50px; object-fit: cover;" 
                 class="rounded border">
          </td>
          <td class="fw-bold">${product.productName}</td>
          <td>${product.brand?.brandName || 'N/A'}</td>
          <td>${product.quantity}</td>
          <td>${product.unit}</td>
          <td>₹${product.mrp}</td>
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

function goToDetails(productId) {
  window.location.href = `productdetails.html?id=${productId}`;
}

loadProducts();
loadProductTypeData();