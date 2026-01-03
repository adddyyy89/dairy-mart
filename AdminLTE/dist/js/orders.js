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