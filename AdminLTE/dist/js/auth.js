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
    const user = typeof getUser === 'function' ? getUser() : null;
    const base = typeof dmApiBase === 'function'
        ? dmApiBase()
        : (localStorage.getItem('dairymartApiBase') || 'http://localhost:8080');
    const finish = () => {
        sessionStorage.removeItem('isLoggedIn');
        sessionStorage.removeItem('user');
        localStorage.removeItem('user');
        const basePath = window.APP_BASE_PATH || './';
        window.location.href = basePath + 'index.html';
    };
    if (user && user.phoneNumber) {
        fetch(base + '/auth/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(user.phoneNumber && user.password
                    ? { Authorization: 'Basic ' + btoa(`${user.phoneNumber}:${user.password}`) }
                    : {}),
            },
            body: JSON.stringify({
                phoneNumber: user.phoneNumber,
                userId: user.userId,
                password: user.password,
            }),
        }).catch(() => {}).finally(finish);
        return;
    }
    finish();
}

window.logout = logout;

//getting userdata

function unwrapUserRecord(raw) {
    if (!raw) return null;
    let user = raw;
    if (typeof unwrap === 'function') {
        user = unwrap(raw);
    } else if (user.map && typeof user.map === 'object') {
        user = user.map;
    }
    return user;
}

function getUser() {
    const userData = sessionStorage.getItem('user') || localStorage.getItem('user');
    if (!userData) return null;
    try {
        return unwrapUserRecord(JSON.parse(userData));
    } catch (_) {
        return null;
    }
}

function sessionUserId() {
    const user = getUser();
    if (!user) return null;
    const raw = user.userId ?? user.userid;
    if (raw === undefined || raw === null || raw === '') return null;
    const id = Number(raw);
    return Number.isFinite(id) ? id : null;
}

window.getUser = getUser;
window.sessionUserId = sessionUserId;
window.unwrapUserRecord = unwrapUserRecord;