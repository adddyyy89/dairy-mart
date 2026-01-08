// immediate auth check 

(function() {
    if (sessionStorage.getItem('isLoggedIn') !== 'true'){
        window.location.href = '/';
    }
}())

//logout function

function logout(){
    sessionStorage.removeItem('isLoggedIn');
    sessionStorage.removeItem('user');

    window.location.href = '/';
}

window.logout = logout;