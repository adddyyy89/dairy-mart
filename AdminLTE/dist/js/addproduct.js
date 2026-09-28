function currentUser() {
  try { return JSON.parse(sessionStorage.getItem('user') || '{}'); } catch (_) { return {}; }
}

async function loadProductTypes() {
  const select = document.getElementById('productTypeId');
  try {
    const data = await apiGet('/product/producttype/getall');
    const types = asList(data);
    select.innerHTML = '<option value="" selected disabled>Select Product Type</option>';
    types.forEach((type) => {
      const option = document.createElement('option');
      option.value = type.productTypeId;
      option.textContent = type.productTypeName;
      select.appendChild(option);
    });
  } catch (error) {
    select.innerHTML = '<option value="">Could not load types</option>';
  }
}

document.addEventListener('DOMContentLoaded', () => {
  loadProductTypes();

  document.getElementById('addProductForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const alertDiv = document.getElementById('alertMessage');
    const user = currentUser();

    const payload = {
      productName: document.getElementById('productName').value.trim(),
      productShortName: document.getElementById('productShortName').value.trim() || document.getElementById('productName').value.trim(),
      productCode: document.getElementById('productCode').value.trim(),
      hsn: document.getElementById('hsn').value.trim(),
      productTypeId: parseInt(document.getElementById('productTypeId').value, 10),
      brandId: parseInt(document.getElementById('brandId').value, 10) || 1,
      productPurchaseRate: document.getElementById('productPurchaseRate').value.trim(),
      productSaleRate: document.getElementById('productSaleRate').value.trim(),
      mrp: document.getElementById('mrp').value.trim(),
      quantity: document.getElementById('quantity').value.trim(),
      unit: document.getElementById('unit').value.trim(),
      igst: document.getElementById('igst').value.trim() || '0',
      productPictureUrl: document.getElementById('productPictureUrl').value.trim(),
      isActive: document.getElementById('isActive').checked,
      createdBy: user.userId || 1,
    };

    try {
      await apiPost('/product/add', payload);
      alertDiv.className = 'alert alert-success mt-3';
      alertDiv.innerHTML = '<i class="bi bi-check-circle me-2"></i>Product added successfully!';
      alertDiv.style.display = 'block';
      document.getElementById('addProductForm').reset();
      document.getElementById('isActive').checked = true;
      setTimeout(() => { window.location.href = './products.html'; }, 1500);
    } catch (error) {
      alertDiv.className = 'alert alert-danger mt-3';
      alertDiv.innerHTML = '<i class="bi bi-exclamation-triangle me-2"></i>' + error.message;
      alertDiv.style.display = 'block';
    }
  });
});
