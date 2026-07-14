/// Maps to RetailOrderDetailsDTO
class OrderLineItem {
  final String productCode;
  final String quantity;
  final String unit;
  final double saleRate;
  final double purchaseRate;
  final String? hsn;
  final DateTime? lastUpdated;
  final String? productName; // enriched client-side from the product catalog

  OrderLineItem({
    required this.productCode,
    required this.quantity,
    required this.unit,
    required this.saleRate,
    required this.purchaseRate,
    this.hsn,
    this.lastUpdated,
    this.productName,
  });

  double get lineTotal => (double.tryParse(quantity) ?? 0) * saleRate;

  factory OrderLineItem.fromJson(Map<String, dynamic> json) {
    double parseNum(dynamic v) {
      if (v == null) return 0;
      if (v is num) return v.toDouble();
      return double.tryParse(v.toString()) ?? 0;
    }

    return OrderLineItem(
      productCode: json['productCode']?.toString() ?? '',
      quantity: json['quantity']?.toString() ?? '0',
      unit: json['unit']?.toString() ?? '',
      saleRate: parseNum(json['saleRate']),
      purchaseRate: parseNum(json['purchaseRate']),
      hsn: json['hsn']?.toString(),
      lastUpdated: DateTime.tryParse(json['lastUpdated']?.toString() ?? ''),
    );
  }

  /// Returns a copy with the product name filled in - used once the order
  /// details screen resolves productCode -> Product via GET /product/get.
  OrderLineItem withProductName(String? name) => OrderLineItem(
        productCode: productCode,
        quantity: quantity,
        unit: unit,
        saleRate: saleRate,
        purchaseRate: purchaseRate,
        hsn: hsn,
        lastUpdated: lastUpdated,
        productName: name,
      );

  Map<String, dynamic> toJson() => {
        'productCode': productCode,
        'quantity': quantity,
        'unit': unit,
        'hsn': hsn ?? '',
        'saleRate': saleRate.toString(),
        'purchaseRate': purchaseRate.toString(),
      };
}

/// Order status - the backend returns an OrderStatusDTO {statusId, statusDesc}.
/// Confirmed against public.orderstatus: 1=NEW, 2=CONFIRMED, 3=REJECTED,
/// 4=DISPATCHED, 5=DELIVERED, 6=RETURNED, 7=CANCELLED.
class OrderStatus {
  final int id;
  final String description;
  const OrderStatus({required this.id, required this.description});

  factory OrderStatus.fromJson(Map<String, dynamic> json) => OrderStatus(
        // NOTE: OrderStatusDTO's own id field is "statusId", not
        // "orderStatusId" (that name is only used on RetailOrderDTO itself,
        // for the FK). Confirmed against OrderStatusDTO.java.
        id: json['statusId'] ?? 0,
        description: json['statusDesc'] ?? 'NEW',
      );
}

/// Maps to RetailOrderDTO
class RetailOrder {
  final int orderId;
  final DateTime orderDate;
  final int retailerId;
  final String? retailerShopName;
  final int branchId;
  final int createdBy;
  final int orderStatusId;
  final String statusDescription;
  final List<OrderLineItem> orderDetails;

  RetailOrder({
    required this.orderId,
    required this.orderDate,
    required this.retailerId,
    this.retailerShopName,
    required this.branchId,
    required this.createdBy,
    required this.orderStatusId,
    required this.statusDescription,
    required this.orderDetails,
  });

  double get totalAmount =>
      orderDetails.fold(0, (sum, item) => sum + item.lineTotal);

  int get totalUnits => orderDetails.fold(
      0, (sum, item) => sum + (double.tryParse(item.quantity) ?? 0).round());

  factory RetailOrder.fromJson(Map<String, dynamic> json) {
    final retailer = json['retailer'] as Map<String, dynamic>?;
    final status = json['status'] as Map<String, dynamic>?;
    final details = (json['orderDetails'] as List<dynamic>?) ?? [];

    return RetailOrder(
      orderId: json['orderId'] ?? 0,
      orderDate: DateTime.tryParse(json['orderDate']?.toString() ?? '') ??
          DateTime.now(),
      retailerId: json['retailerId'] ?? 0,
      retailerShopName: retailer?['shopName'],
      branchId: json['branchId'] ?? 0,
      createdBy: json['createdBy'] ?? 0,
      orderStatusId: json['orderStatusId'] ?? 0,
      statusDescription: status != null
          ? OrderStatus.fromJson(status).description
          : 'NEW',
      orderDetails:
          details.map((d) => OrderLineItem.fromJson(d)).toList(growable: false),
    );
  }

  /// Body shape expected by POST /retailorder/add
  Map<String, dynamic> toCreateJson({
    required int retailerId,
    required int branchId,
    required int createdBy,
  }) =>
      {
        'retailerId': retailerId,
        'branchId': branchId,
        'createdBy': createdBy,
        'orderDate': DateTime.now().toIso8601String().split('T').first,
        'orderDetails': orderDetails.map((e) => e.toJson()).toList(),
      };

  /// Body shape expected by POST /retailorder/update - the endpoint expects
  /// the *whole* order payload (retailerId, branchId, createdBy,
  /// orderDetails), not just the changed field, so this repopulates
  /// everything from the existing order and only swaps in [newStatusId].
  ///
  /// NOTE: the confirmed sample body didn't include "orderId" at the top
  /// level, but RetailOrderDTO does have its own orderId field, and nothing
  /// else in the payload identifies which order to update - so it's included
  /// here as well. Drop it if the backend actually rejects it.
  ///
  /// Each line item also needs its own "orderId" - RetailOrderDetailsDTO has
  /// that as its own field (confirmed in the backend source), separate from
  /// OrderLineItem's other properties. Every detail belongs to this same
  /// order, so the parent orderId is stamped onto each one here rather than
  /// tracking it separately per line item.
  Map<String, dynamic> toUpdateJson({required int newStatusId}) => {
        'orderId': orderId,
        'retailerId': retailerId,
        'branchId': branchId,
        'createdBy': createdBy,
        'orderStatusId': newStatusId,
        'orderDetails': orderDetails
            .map((e) => {'orderId': orderId, ...e.toJson()})
            .toList(),
      };

  /// Local copy with just the status swapped - used to optimistically reflect
  /// a status change in the order list immediately, without waiting on a
  /// full re-fetch from the server.
  RetailOrder withStatus({required int statusId, required String statusDescription}) =>
      RetailOrder(
        orderId: orderId,
        orderDate: orderDate,
        retailerId: retailerId,
        retailerShopName: retailerShopName,
        branchId: branchId,
        createdBy: createdBy,
        orderStatusId: statusId,
        statusDescription: statusDescription,
        orderDetails: orderDetails,
      );
}