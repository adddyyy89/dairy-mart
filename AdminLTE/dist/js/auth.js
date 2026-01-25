// immediate auth check 
(function () {
    const isLoginPage = window.location.pathname.endsWith('index.html') || window.location.pathname.endsWith('/');

    if (!isLoginPage) {
        if (sessionStorage.getItem('isLoggedIn') !== 'true') {
            window.location.href = '/';
        }

    }
    else {
        if (sessionStorage.getItem('isLoggedIn') === 'true') {
            window.location.href = './home.html';
        }
    }

}())

//logout function

function logout() {
    sessionStorage.removeItem('isLoggedIn');
    sessionStorage.removeItem('user');
    const basePath = window.APP_BASE_PATH || './';

    window.location.href = basePath + 'index.html';
}

window.logout = logout;

//getting userdata

function getUser() {
    const userData = sessionStorage.getItem('user');
    if (userData) {
        return JSON.parse(userData);

    }
    return null;
}

window.getUser = getUser;