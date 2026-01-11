function loadSidebar(basePath = './') {
    const sidebarHTML = `
      <!--begin::Sidebar Brand-->
      <div class="sidebar-brand">
        <!--begin::Brand Link-->
        <a href="${basePath}home.html" class="brand-link">
          <!--begin::Brand Image-->
          <img src="${basePath}assets/img/AdminLTELogo.png" alt="AdminLTE Logo" class="brand-image opacity-75 shadow" />
          <!--end::Brand Image-->
          <!--begin::Brand Text-->
          <span class="brand-text fw-light">Diary Mart</span>
          <!--end::Brand Text-->
        </a>
        <!--end::Brand Link-->
      </div>
      <!--end::Sidebar Brand-->
      <!--begin::Sidebar Wrapper-->
      <div class="sidebar-wrapper">
        <nav class="mt-2">
          <!--begin::Sidebar Menu-->
          <ul class="nav sidebar-menu flex-column" data-lte-toggle="treeview" role="navigation"
            aria-label="Main navigation" data-accordion="false" id="navigation">
            <li class="nav-item">
              <a href="${basePath}home.html" class="nav-link">
                <i class="nav-icon bi bi-circle"></i>
                <p>Dairy Mart</p>
              </a>
            </li>
            <li class="nav-item">
              <a href="${basePath}pages/users.html" class="nav-link">
                <i class="nav-icon bi bi-circle"></i>
                <p>Users</p>
              </a>
            </li>
            <li class="nav-item">
              <a href="${basePath}pages/orders.html" class="nav-link">
                <i class="nav-icon bi bi-circle"></i>
                <p>Orders</p>
              </a>
            </li>
            <li class="nav-item">
              <a href="${basePath}pages/ledgers.html" class="nav-link">
                <i class="nav-icon bi bi-circle"></i>
                <p>Ledgers</p>
              </a>
            </li>
            <li class="nav-item">
              <a href="${basePath}pages/products.html" class="nav-link">
                <i class="nav-icon bi bi-circle"></i>
                <p>Products</p>
              </a>
            </li>
          </ul>
          <!--end::Sidebar Menu-->
        </nav>
      </div>
      <!--end::Sidebar Wrapper-->
    
    `

    document.getElementById('sidebar-container').innerHTML = sidebarHTML;
}