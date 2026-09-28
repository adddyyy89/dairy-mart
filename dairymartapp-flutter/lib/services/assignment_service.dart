import '../models/assignment.dart';
import '../utils/json_unwrap.dart';
import 'api_client.dart';
import 'api_config.dart';

class AssignmentService {
  AssignmentService._();
  static final AssignmentService instance = AssignmentService._();

  Future<List<SalesmanAssignment>> getForSalesman(int salesmanId) async {
    final json =
        await ApiClient.instance.get(ApiConfig.getSalesmanAssignments(salesmanId));
    return asList(json)
        .map((e) => SalesmanAssignment.fromJson(asMap(e)))
        .toList();
  }

  Future<ShopSummary?> getShopForRetailer(int userId) async {
    final json = await ApiClient.instance.get(ApiConfig.shopGetByUser(userId));
    final shops = asList(json);
    if (shops.isNotEmpty) return ShopSummary.fromJson(asMap(shops.first));
    if (json is Map && asMap(json)['shopId'] != null) {
      return ShopSummary.fromJson(asMap(json));
    }
    return null;
  }

  /// Shop id + branch id needed by POST /retailorder/add.
  /// `retailerId` on an order is the shop id. Branch is not on ShopDTO;
  /// the native app sent 7, otherwise we copy it from the salesman assignment.
  Future<ShopSummary> getOrderShopForRetailer(int userId) async {
    var shop = await getShopForRetailer(userId);
    if (shop == null || shop.shopId <= 0) {
      final json = await ApiClient.instance.get(ApiConfig.shopGetAll);
      for (final row in asList(json)) {
        final candidate = ShopSummary.fromJson(asMap(row));
        if (candidate.userId == userId) {
          shop = candidate;
          break;
        }
      }
    }
    if (shop == null || shop.shopId <= 0) {
      throw ApiException(
        'No shop is linked to this retailer account. Ask admin to add a shop.',
      );
    }

    var branchId = shop.branchId;
    if (branchId <= 0) {
      try {
        final json =
            await ApiClient.instance.get(ApiConfig.getAllSalesmanToRetail);
        for (final row in asList(json)) {
          final assignment = SalesmanAssignment.fromJson(asMap(row));
          if (assignment.shopId == shop.shopId && assignment.branchId > 0) {
            branchId = assignment.branchId;
            break;
          }
        }
      } catch (_) {}
    }
    if (branchId <= 0) branchId = 7;

    return ShopSummary(
      shopId: shop.shopId,
      userId: shop.userId,
      shopName: shop.shopName,
      branchId: branchId,
      gstId: shop.gstId,
      addressId: shop.addressId,
      gstNumber: shop.gstNumber,
      panNumber: shop.panNumber,
      aadharNumber: shop.aadharNumber,
    );
  }

  Future<ShopSummary> updateShop(ShopSummary shop) async {
    final json = await ApiClient.instance.post(
      ApiConfig.shopUpdate,
      body: {
        'shopId': shop.shopId,
        'shopName': shop.shopName,
        'userId': shop.userId,
        'addressId': shop.addressId,
        'gstId': shop.gstId,
        'isActive': true,
        'gst': {
          'gstId': shop.gstId,
          'gstNumber': shop.gstNumber,
          'panNumber': shop.panNumber,
          'aadharNumber': shop.aadharNumber,
        },
      },
    );
    return ShopSummary.fromJson(asMap(json));
  }
}
