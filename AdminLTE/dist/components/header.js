function loadHeader(basePath) {
    const headerHTML = `
      <div class="container-fluid">
        <ul class="navbar-nav">
          <li class="nav-item">
            <a class="nav-link" data-lte-toggle="sidebar" href="#" role="button">
              <i class="bi bi-list"></i>
            </a>
          </li>
          <li class="nav-item d-none d-md-block"><a href="${basePath}/home.html" class="nav-link">Home</a></li>
        </ul>
        <ul class="navbar-nav ms-auto">
          <li class="nav-item dropdown">
            <a class="nav-link" data-bs-toggle="dropdown" href="#" id="notif-toggle">
              <i class="bi bi-bell-fill"></i>
              <span class="navbar-badge badge text-bg-warning" id="notif-badge" style="display:none">0</span>
            </a>
            <div class="dropdown-menu dropdown-menu-lg dropdown-menu-end" id="notif-menu">
              <span class="dropdown-item dropdown-header" id="notif-header">Notifications</span>
              <div class="dropdown-divider"></div>
              <div id="notif-items">
                <span class="dropdown-item text-secondary">Loading...</span>
              </div>
              <div class="dropdown-divider"></div>
              <a href="${basePath}pages/notifications.html" class="dropdown-item dropdown-footer">See all notifications</a>
            </div>
          </li>
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" aria-expanded="false">
              <i class="bi bi-person-circle me-1"></i>
              <span id="userName">Admin</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end">
              <li><a class="dropdown-item" href="${basePath}pages/settings.html"><i class="bi bi-gear me-2"></i>Settings</a></li>
              <li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item" href="#" onclick="logout()"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
            </ul>
          </li>
        </ul>
      </div>
    `;

    document.getElementById('header-container').innerHTML = headerHTML;
}

async function refreshHeaderNotifications() {
    const badge = document.getElementById('notif-badge');
    const items = document.getElementById('notif-items');
    const header = document.getElementById('notif-header');
    if (typeof apiGet !== 'function') {
        if (items) items.innerHTML = '<span class="dropdown-item text-secondary">Notifications are unavailable on this page.</span>';
        return;
    }
    const userId = typeof ensureSessionUserId === 'function'
        ? await ensureSessionUserId()
        : (typeof sessionUserId === 'function' ? sessionUserId() : 0);
    if (userId == null) {
        if (badge) badge.style.display = 'none';
        if (items) items.innerHTML = '<span class="dropdown-item text-secondary">Could not resolve your admin user id. Log out and sign in with a real admin account.</span>';
        return;
    }
    try {
        const list = asList(await apiGet('/notification/get/' + userId)).slice(0, 8);
        const unread = list.filter((n) => n.isRead !== true && n.read !== true).length;
        if (badge) {
            if (unread > 0) {
                badge.style.display = '';
                badge.textContent = String(unread);
            } else {
                badge.style.display = 'none';
            }
        }
        if (header) header.textContent = unread + ' unread notification' + (unread === 1 ? '' : 's');
        if (items) {
            if (!list.length) {
                items.innerHTML = '<span class="dropdown-item text-secondary">No notifications yet.</span>';
            } else {
                items.innerHTML = list.map((n) => {
                    const icon = n.kind === 'LEDGER' ? 'bi-wallet2' : 'bi-receipt';
                    const title = (n.title || 'Update').replace(/</g, '');
                    const message = (n.message || '').replace(/</g, '');
                    return `<a href="${window.APP_BASE_PATH || './'}pages/notifications.html" class="dropdown-item">
                      <i class="bi ${icon} me-2"></i> ${title}
                      <div class="text-secondary fs-7">${message}</div>
                    </a><div class="dropdown-divider"></div>`;
                }).join('');
            }
        }
    } catch (error) {
        if (items) {
            items.innerHTML = '<span class="dropdown-item text-danger">Could not load notifications.</span>';
        }
        console.warn('Notifications:', error.message || error);
    }
}

window.refreshHeaderNotifications = refreshHeaderNotifications;
