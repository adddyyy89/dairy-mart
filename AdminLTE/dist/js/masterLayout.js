function loadLayout(options = {}) {
    const basePath = options.basePath || './';

    window.APP_BASE_PATH = basePath;

    loadHeader(basePath);
    loadSidebar(basePath);
    loadFooter();

    // Display user name
    const user = getUser();
    if (user && user.name) {
        const userNameElement = document.getElementById('userName');
        if (userNameElement) userNameElement.textContent = user.name;
    }

    initScrollbars();
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