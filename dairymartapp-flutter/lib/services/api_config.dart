/// Centralised endpoint map, copied 1:1 from DairyMart_postman_collection.json
/// so nothing has to be re-guessed when wiring screens up.
///
/// Swap [baseUrl] per environment. The legacy Android app pointed at
/// http://localhost:8080 for local dev and the ElasticBeanstalk host for
/// "aws" requests - do the same via --dart-define or a build flavor.
class ApiConfig {
  ApiConfig._();

  static const String baseUrl = String.fromEnvironment(
    'DAIRYMART_API_BASE_URL',
    defaultValue: 'http://localhost:8080',
  );

  // Auth
  static const String login = '/auth/login';
  static const String logout = '/auth/logout';

  // User
  static const String userAdd = '/user/add';
  static String userGet(int id) => '/user/get/$id';
  static const String userGetAll = '/user/get/all';
  static String userGetByType(int typeId) => '/user/get/usertype/$typeId';
  static const String userUpdate = '/user/update';

  // Shop (retailer profile)
  static const String shopAdd = '/shop/add';
  static String shopGet(int id) => '/shop/get/$id';
  static const String shopGetAll = '/shop/get/all';
  static String shopGetByUser(int userId) => '/shop/get/user/$userId';

  // Product catalog
  static const String productAdd = '/product/add';
  static const String productUpdate = '/product/update';
  static const String productGetAll = '/product/getall';
  static String productGetById(int id) => '/product/get/$id';
  static String productGetByType(int typeId) =>
      '/product/get/producttype/$typeId';

  // Crates
  static const String crateUpdate = '/crate/update';
  static const String crateGetAll = '/crate/get/all';
  static String crateGetByUser(int userId) => '/crate/get/user/$userId';
  static String crateAssignedToUser(int userId) =>
      '/crate/assigned/user/$userId';

  // Salesman <-> Retailer assignment
  static const String assignSalesmanToRetail = '/salesmantoretail/assign';
  static const String deleteSalesmanToRetail = '/salesmantoretail/delete';
  static const String getAllSalesmanToRetail = '/salesmantoretail/get/all';
  static String getSalesmanAssignments(int salesmanId) =>
      '/salesmantoretail/get/assignment/salesman/$salesmanId';

  // Dashboards
  static String salesmanDashboard(int userId) =>
      '/salesman/dashboard/get/$userId';
  static const String adminDashboard = '/admin/dashboard/get';
  static const String adminUsers = '/admin/users/get';
  static const String adminLedgers = '/admin/ledgers/get';
  static String retailerDashboard(int userId) =>
      '/retailer/dashboard/get/$userId';

  // Ledger
  static const String ledgerUnassigned = '/ledger/get/unassigned';
  static String ledgerForSalesman(int salesmanId) =>
      '/ledger/salesman/get/$salesmanId';
  static const String ledgerSalesmanUpdate = '/ledger/salesman/update';
  static const String ledgerAdd = '/ledger/add';

  // Orders
  static const String orderAdd = '/retailorder/add';
  static const String orderUpdate = '/retailorder/update';
  static const String orderGetAll = '/retailorder/get/all';
  static String orderGetBySalesman(int salesmanId) =>
      '/retailorder/get/salesman/$salesmanId';
  static String orderGetByRetailer(int retailerId) =>
      '/retailorder/get/retailer/$retailerId';

  // Reference data
  static const String statesAll = '/address/state/get/all';
  static String citiesByState(int stateId) =>
      '/address/city/getbystate/$stateId';
  static const String userTypeGetAll = '/usertype/get/all';
}