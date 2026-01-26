let productTypes = [];

async function loadProductTypeData() {

    try {
        const response = await fetch(`http://localhost:8080/product/producttype/getall`);
        if (!response.ok) throw new Error("Product types not found");
        
        const data = await response.json();
        productTypes = data; 
    } catch (error) {
        console.error("Error:", error);
        alert("Could not load product details.");
    }
}

async function populateProductTypeDropdown() {
    const productTypeField = document.getElementById('productTypeId');

    productTypeField.innerHTML = `<option value="" disabled selected>Select Product Type</option>`;

    productTypes.forEach(productType => {
        const option = document.createElement('option');
        option.value = productType.productTypeId;
        option.textContent = productType.productTypeName;
        
        productTypeField.appendChild(option);
    })
}

window.onload = async function() {
    await loadProductTypeData();  
    populateProductTypeDropdown();  
};


document.getElementById('addProductForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const formEntries = Object.fromEntries(formData.entries());
    
    const selectedProductType = productTypes.find(productType => productType.productTypeId == formEntries.productTypeId );
    const productData = {
        // productId: ,
        hsn: formEntries.hsn,
        productName: formEntries.productName,
        productShortName: formEntries.productShortName,
        productPurchaseRate: formEntries.productPurchaseRate,
        productSaleRate: formEntries.productSaleRate,
        mrp: formEntries.mrp,
        quantity: formEntries.quantity,
        unit: formEntries.unit,
        productCode: formEntries.productCode,
        // igst: ,
        // productPictureUrl: , 
        // createdBy: ,
        isActive: formEntries.isActive === 'on',
        productTypeId: parseInt(formEntries.productTypeId),
        brandId: parseInt(formEntries.brandId),
        type: {
            productTypeId: parseInt(formEntries.productTypeId),
            productTypeName: selectedProductType.productTypeName
        },
        brand: {
            brandId: parseInt(formEntries.brandId),
            brandName: formEntries.brandName
        }
    };
    console.log('New Product Data :', productData);
});