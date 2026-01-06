document.getElementById('addUserForm').addEventListener('submit', async function(e) {
      e.preventDefault();
      
      const alertDiv = document.getElementById('alertMessage');
      
      const userData = {
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value || '',
        phoneNumber: document.getElementById('phoneNumber').value,
        userTypeId: parseInt(document.getElementById('userTypeId').value),
        emailId: document.getElementById('emailId').value,
        password: document.getElementById('password').value,
        isActive: document.getElementById('isActive').checked,
        address: {
          fullAddress: document.getElementById('fullAddress').value,
          cityId: parseInt(document.getElementById('cityId').value) || 0
        }
      };
      
      try {
        const response = await fetch('http://localhost:8080/user/add', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(userData)
        });
        
        if (response.ok) {
          alertDiv.className = 'alert alert-success mt-3';
          alertDiv.innerHTML = '<i class="bi bi-check-circle me-2"></i>User added successfully!';
          alertDiv.style.display = 'block';
          
          // Reset form
          document.getElementById('addUserForm').reset();
          
          // Redirect after 2 seconds
          setTimeout(() => {
            window.location.href = './users.html';
          }, 2000);
        } else {
          const errorData = await response.json();
          throw new Error(errorData.message || 'Failed to add user');
        }
      } catch (error) {
        alertDiv.className = 'alert alert-danger mt-3';
        alertDiv.innerHTML = '<i class="bi bi-exclamation-triangle me-2"></i>' + error.message;
        alertDiv.style.display = 'block';
      }
    });