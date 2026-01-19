// Filter by User Type Button
document.addEventListener('DOMContentLoaded', () => {
    const rows = document.querySelectorAll('.clickable-row');
    rows.forEach(row => {
        row.addEventListener('click', () => {
            window.location.href = "user" + row.dataset.href;
        });
    });
});

function filterTable(type) {
    const table = document.getElementById("userTable");
    const tr = table.getElementsByTagName("tr");

    for (let i = 1; i < tr.length; i++) {
        const typeColumn = tr[i].getElementsByTagName("td")[1]; // User Type is 2nd column
        if (typeColumn) {
            const textValue = typeColumn.textContent || typeColumn.innerText;
            if (type === "Show All" || textValue.trim() === type) {
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
    const table = document.getElementById("userTable");
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

async function getData() {
  const url = "http://localhost:8080/admin/users/get";
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    const json = await response.json();
    console.log(json);
    
    const tableBody = document.getElementById("users-table");
    tableBody.innerHTML = "";

    json.map.users.myArrayList.forEach(user => {
        let row = ``;
        if(user.map.active) {
            row = `
        <tr class="clickable-row">
                    <td><a href="userdetails.html?id=${user.map.userId}" 
               class="link-primary link-offset-2 link-underline-opacity-25 link-underline-opacity-100-hover">${user.map.firstName}</a></td>
                    <td><span class="badge bg-info text-dark">${user.map.type.map.userTypeDesc}</span></td>
                    <td>${user.map.phoneNumber}</td>
                    <td>${user.map.emailId}</td>
                    <td>${user.map.address.map.fullAddress}</td>
                </tr>
      `;
        }
        else {
            row = `
        <tr class="clickable-row disabled-row">
                    <td><a href="userdetails.html?id=${user.map.userId}" 
               class="link-primary link-offset-2 link-underline-opacity-25 link-underline-opacity-100-hover">${user.map.firstName}</a></td>
                    <td><span class="badge bg-info text-dark">${user.map.type.map.userTypeDesc}</span></td>
                    <td>${user.map.phoneNumber}</td>
                    <td>${user.map.emailId}</td>
                    <td>${user.map.address.map.fullAddress}</td>
                </tr>
      `;
        }
      
      // Append the row to the table
      tableBody.innerHTML += row;
    });
    
  } catch (error) {
    console.error(error.message);
  }
}

// Call the function
getData();