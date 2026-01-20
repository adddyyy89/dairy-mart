/**
 * mappingHandler.js
 * Logic to read sessionStorage and populate UI components
 */

document.addEventListener('DOMContentLoaded', () => {
    populatePageFromSession();
});

function populatePageFromSession() {
    // 1. Retrieve and Parse Data
    const rawData = sessionStorage.getItem('salesmanRetailerDataMap'); // Ensure key matches your storage key
    if (!rawData) {
        console.warn("No mapping data found in sessionStorage.");
        return;
    }

    try {
        const mappings = JSON.parse(rawData);
        
        // Selectors
        const salesmanDropdown = document.getElementById('salesmanSelect');
        const retailerDropdown = document.getElementById('retailerSelect');
        const tableBody = document.getElementById('mappingTableBody');

        // Clear existing static/demo content
        salesmanDropdown.innerHTML = '<option value="" selected disabled>Choose a salesman...</option>';
        retailerDropdown.innerHTML = '<option value="" selected disabled>Choose a retailer...</option>';
        tableBody.innerHTML = '';

        // Sets to track unique entities for dropdowns
        const uniqueSalesmen = new Map();
        const uniqueRetailers = new Map();

        // 2. Iterate through the JSON Object
        Object.keys(mappings).forEach(key => {
            const item = mappings[key];
            const salesman = item.salesman;
            const retailer = item.retailer;

            // Track unique entries for the dropdowns
            uniqueSalesmen.set(salesman.userId, `${salesman.firstName} ${salesman.lastName}`);
            uniqueRetailers.set(retailer.shopId, retailer.shopName);

            // 3. Populate Table Row
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
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="editMapping('${key}')">
                        <i class="bi bi-pencil-square"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteMapping('${key}')">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            `;
            tableBody.appendChild(row);
        });

        // 4. Populate Dropdowns
        uniqueSalesmen.forEach((name, id) => {
            const opt = document.createElement('option');
            opt.value = id;
            opt.textContent = name;
            salesmanDropdown.appendChild(opt);
        });

        uniqueRetailers.forEach((name, id) => {
            const opt = document.createElement('option');
            opt.value = id;
            opt.textContent = name;
            retailerDropdown.appendChild(opt);
        });

    } catch (error) {
        console.error("Error parsing session data:", error);
    }
}

/**
 * Placeholder functions for Edit/Delete
 */
function editMapping(mappingId) {
    console.log("Editing mapping ID:", mappingId);
    // Implementation: Fetch object from session, fill the form, change 'Assign' button to 'Update'
}

function deleteMapping(mappingId) {
    if(confirm("Are you sure you want to remove this assignment?")) {
        console.log("Deleting mapping ID:", mappingId);
        // Implementation: Remove from session storage and refresh UI
    }
}