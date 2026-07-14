import '../models/order.dart';
import 'api_client.dart';
import 'api_config.dart';

class OrderService {
  OrderService._();
  static final OrderService instance = OrderService._();

  Future<List<RetailOrder>> getOrdersForSalesman(int salesmanId) async {
    final json =
        await ApiClient.instance.get(ApiConfig.orderGetBySalesman(salesmanId));
    return (json as List<dynamic>)
        .map((e) => RetailOrder.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<List<RetailOrder>> getOrdersForRetailer(int retailerId) async {
    final json =
        await ApiClient.instance.get(ApiConfig.orderGetByRetailer(retailerId));
    return (json as List<dynamic>)
        .map((e) => RetailOrder.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<List<RetailOrder>> getAllOrders() async {
    final json = await ApiClient.instance.get(ApiConfig.orderGetAll);
    return (json as List<dynamic>)
        .map((e) => RetailOrder.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<void> createOrder({
    required int retailerId,
    required int branchId,
    required int createdBy,
    required List<OrderLineItem> items,
  }) async {
    await ApiClient.instance.post(
      ApiConfig.orderAdd,
      body: {
        'retailerId': retailerId,
        'branchId': branchId,
        'createdBy': createdBy,
        'orderDate': DateTime.now().toIso8601String().split('T').first,
        'orderDetails': items.map((e) => e.toJson()).toList(),
      },
    );
  }

  /// Used by the salesman to move an order through its lifecycle
  /// (e.g. PENDING -> IN PROGRESS -> DELIVERED), matching the status chips
  /// seen in activity_salesman_activity_orders.xml.
  ///
  /// POST /retailorder/update expects the *entire* order payload back
  /// (retailerId, branchId, createdBy, orderDetails), not just the changed
  /// status - confirmed against a real Postman example. [order] supplies all
  /// of that; only the status id actually changes.
  Future<void> updateOrderStatus({
    required RetailOrder order,
    required int newStatusId,
  }) async {
    await ApiClient.instance.post(
      ApiConfig.orderUpdate,
      body: order.toUpdateJson(newStatusId: newStatusId),
    );
  }
}