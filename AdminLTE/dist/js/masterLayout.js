function loadLayout(options = {}) {
    const basePath = options.basePath || './';

    window.APP_BASE_PATH = basePath;

    loadHeader(basePath);
    loadSidebar(basePath);
    loadFooter();

    const user = getUser();
    if (user && user.name) {
        const userNameElement = document.getElementById('userName');
        if (userNameElement) userNameElement.textContent = user.name;
    }

    if (typeof refreshHeaderNotifications === 'function') {
        refreshHeaderNotifications();
        const poll = Math.max(15, Number(dmSettings().headerNotifSeconds || 60)) * 1000;
        setInterval(refreshHeaderNotifications, poll);
    }

    startPageAutoRefresh();

    initScrollbars();
}

function startPageAutoRefresh() {
    const settings = typeof dmSettings === 'function' ? dmSettings() : {};
    if (!settings.pageRefreshEnabled) return;
    const seconds = Number(settings.pageRefreshSeconds || 0);
    if (!Number.isFinite(seconds) || seconds < 15) return;
    const path = (window.location.pathname || '').toLowerCase();
    if (path.endsWith('index.html') || path.endsWith('settings.html')) return;
    setInterval(() => {
        const tag = document.activeElement && document.activeElement.tagName;
        if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT') return;
        window.location.reload();
    }, seconds * 1000);
}

function initScrollbars() {
    const SELECTOR_SIDEBAR_WRAPPER = '.sidebar-wrapper';
    const sidebarWrapper = document.querySelector(SELECTOR_SIDEBAR_WRAPPER);
    if (sidebarWrapper && typeof OverlayScrollbarsGlobal !== 'undefined') {
        OverlayScrollbarsGlobal.OverlayScrollbars(sidebarWrapper, {
            scrollbars: {
                theme: 'os-theme-light',
                autoHide: 'leave',
                clickScroll: true,
            },
        });
    }
}