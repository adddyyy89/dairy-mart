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

    // try {
    //     const response = await fetch('http://localhost:8080/product/add', {
    //         method: 'POST',
    //         headers: { 'Content-Type': 'application/json' },
    //         body: JSON.stringify(productData)
    //     });

    //     if (response.ok) {
    //         alert('Product added successfully!');
    //     } else {
    //         alert('Failed.');
    //     }
    // } catch (error) {
    //     console.error('Error:', error);
    // }
});


// Product Image Preview 
document.getElementById('productPicture').addEventListener('change', function(event) {
    const file = event.target.files[0];
    const previewImg = document.getElementById('previewImg');
    const imagePreview = document.getElementById('imagePreview');
    
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            previewImg.src = e.target.result;
            imagePreview.style.display = 'block';
        };
        reader.readAsDataURL(file);
    } else {
        imagePreview.style.display = 'none';
    }
});