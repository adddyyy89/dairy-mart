let originalData = {};

async function loadProductData() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    
    try {
        const response = await fetch(`http://localhost:8080/product/get/${id}`);
        if (!response.ok) throw new Error("Product not found");
        
        const data = await response.json();
        originalData = data; 
        populateForm(data);
    } catch (error) {
        console.error("Error:", error);
        alert("Could not load product details.");
    }
}

function populateForm(data) {
    // 1. Fill standard top-level fields (productId, hsn, productName, etc.)
    const fields = [
        'productId', 'hsn', 'productName', 'productShortName', 
        'productPurchaseRate', 'productSaleRate', 'mrp', 
        'quantity', 'unit', 'productCode', 'igst', 'brandId', 'productTypeId'
    ];

    fields.forEach(key => {
        const input = document.getElementById(key);
        if (input) input.value = data[key] ?? "";
    });

    // 2. Fill Nested Objects
    if (data.brand) {
        document.getElementById('brandName').value = data.brand.brandName ?? "";
    }
    if (data.type) {
        document.getElementById('productTypeName').value = data.type.productTypeName ?? "";
    }

    // 3. Handle Image Preview
    const imgPreview = document.getElementById('productImagePreview');
    if (imgPreview) {
        imgPreview.src = data.productPictureUrl || 'placeholder.jpg';
    }
}

// TOGGLE EDIT MODE (Same as previous step)
function toggleEditMode() {
    const inputs = document.querySelectorAll('.editable-field');
    // We don't want to edit IDs or internal names usually, but for this demo:
    inputs.forEach(input => {
        // Exclude productId from being edited if desired
        if (input.id !== 'productId') {
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

    // NOTE: If your backend expects the nested 'brand' and 'type' objects 
    // to be sent back, you may need to reconstruct them here.
    
    try {
        const response = await fetch('http://localhost:8080/product/update', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updatedData)
        });

        if (response.ok) {
            alert('Product updated successfully!');
            location.reload(); // Refresh to show updated view
        } else {
            alert('Update failed.');
        }
    } catch (error) {
        console.error('Error:', error);
    }
});

window.onload = loadProductData;