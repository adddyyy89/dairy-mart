// login using dummy data, REMOVE BEFORE PRODUCTION
const dummy_user = {
    phoneNumber: '1234567890' ,
    password: 'admin'
}

document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const alertDiv = document.getElementById('alertMessage');
      
    const loginData = {
        phoneNumber: document.getElementById('phoneNumber').value,
        password: document.getElementById('loginPassword').value
    }

    // login using dummy data, REMOVE BEFORE PRODUCTION

    if(loginData.phoneNumber === dummy_user.phoneNumber && loginData.password === dummy_user.password){
        const dummyUser = {
            phoneNumber: loginData.phoneNumber,
            name:'Admin Kumar',
            isActive: true
        }

        sessionStorage.setItem('isLoggedIn','true');
        sessionStorage.setItem('user',JSON.stringify(dummyUser));

        alertDiv.className = 'alert alert-success mt-3';
        alertDiv.innerHTML = '<i class="bi bi-check-circle me-2"></i> Login successful! Redirecting...';
        alertDiv.style.display = 'block';

        setTimeout(() => {
            window.location.href='./home.html';
        },1500);

        return;
    }

    // Check from database

    try{
        const response = await fetch('http://localhost:8080/auth/login', {
            method: 'POST',
            headers: {
                'Content-type' : 'application/json'
            },
            body: JSON.stringify(loginData)
        });

        let responseData;

        if(response.ok){
            responseData = await response.json()
        }else {
            const errorData = await response.json().catch(() => null);
            throw new Error(errorData?.message || "Failed to verify login details");
        }

        if(responseData.isActive){
            localStorage.setItem('user',JSON.stringify(responseData))
            sessionStorage.setItem('isLoggedIn','true');
            sessionStorage.setItem('user',JSON.stringify(responseData));
            window.location.href = './home.html'
        }else{
            alertDiv.className = 'alert alert-danger mt-3';
            alertDiv.innerHTML = '<i class="bi bi-exclamation-triangle me-2"></i> Incorrect Credentials';
            alertDiv.style.display = 'block';

            document.getElementById('phoneNumber').value = ''
            document.getElementById('loginPassword').value = ''
        }
    }catch (error) {
        alertDiv.className = 'alert alert-danger mt-3';
        alertDiv.innerHTML = '<i class="bi bi-exclamation-triangle me-2"></i>' + error.message;
        alertDiv.style.display = 'block';

        document.getElementById('phoneNumber').value = ''
        document.getElementById('loginPassword').value = ''
    }
    
});

