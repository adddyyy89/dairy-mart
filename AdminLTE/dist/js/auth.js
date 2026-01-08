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

//getting userdata

function getUser(){
    const userData = sessionStorage.getItem('user');
    if(userData){
        return JSON.parse(userData);

    }
    return null;
}

window.getUser = getUser;