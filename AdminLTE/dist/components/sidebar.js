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
                <i class="nav-icon bi bi-people"></i>
                <p>Users</p>
              </a>
            </li>
            <li class="nav-item">
              <a href="${basePath}pages/salesmen.html" class="nav-link">
                <i class="nav-icon bi bi-person-badge"></i>
                <p>Salesmen</p>
              </a>
            </li>
            <li class="nav-item">
              <a href="${basePath}pages/retailers.html" class="nav-link">
                <i class="nav-icon bi bi-shop"></i>
                <p>Retailers</p>
              </a>
            </li>
            <li class="nav-item">
              <a href="${basePath}pages/salesman-retailer-map.html" class="nav-link">
                <i class="nav-icon bi bi-diagram-3"></i>
                <p>Assign Retailers</p>
              </a>
            </li>
            <li class="nav-item">
              <a href="${basePath}pages/orders.html" class="nav-link">
                <i class="nav-icon bi bi-receipt"></i>
                <p>Orders</p>
              </a>
            </li>
            <li class="nav-item">
              <a href="${basePath}pages/ledgers.html" class="nav-link">
                <i class="nav-icon bi bi-wallet2"></i>
                <p>Ledgers</p>
              </a>
            </li>
            <li class="nav-item">
              <a href="${basePath}pages/products.html" class="nav-link">
                <i class="nav-icon bi bi-box-seam"></i>
                <p>Products</p>
              </a>
            </li>
            <li class="nav-item">
              <a href="${basePath}pages/addproduct.html" class="nav-link">
                <i class="nav-icon bi bi-plus-square"></i>
                <p>Add Product</p>
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