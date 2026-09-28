import '../utils/json_unwrap.dart';

/// One row from GET /salesmantoretail/get/assignment/salesman/{id}.
/// `retailerId` on the API is the **shop** id, not the user id.
class SalesmanAssignment {
  final int shopId;
  final int retailerUserId;
  final int salesmanId;
  final int branchId;
  final String shopName;
  final String vehicleNumber;

  const SalesmanAssignment({
    required this.shopId,
    required this.retailerUserId,
    required this.salesmanId,
    required this.branchId,
    required this.shopName,
    required this.vehicleNumber,
  });

  factory SalesmanAssignment.fromJson(Map<String, dynamic> raw) {
    final json = asMap(raw);
    final shop = asMap(json['retailer']);
    final owner = asMap(shop['owner']);
    return SalesmanAssignment(
      shopId: asInt(json['retailerId'] ?? shop['shopId']),
      retailerUserId: asInt(shop['userId'] ?? owner['userId']),
      salesmanId: asInt(json['salesmanId']),
      branchId: asInt(json['branchId']),
      shopName: (shop['shopName'] ?? 'Shop #${json['retailerId']}').toString(),
      vehicleNumber: json['vehicleNumber']?.toString() ?? '',
    );
  }
}

class ShopSummary {
  final int shopId;
  final int userId;
  final String shopName;
  final int branchId;
  final int gstId;
  final int addressId;
  final String gstNumber;
  final String panNumber;
  final String aadharNumber;

  const ShopSummary({
    required this.shopId,
    required this.userId,
    required this.shopName,
    this.branchId = 0,
    this.gstId = 0,
    this.addressId = 0,
    this.gstNumber = '',
    this.panNumber = '',
    this.aadharNumber = '',
  });

  factory ShopSummary.fromJson(Map<String, dynamic> raw) {
    final json = asMap(raw);
    final gst = asMap(json['gst']);
    return ShopSummary(
      shopId: asInt(json['shopId']),
      userId: asInt(json['userId']),
      shopName: json['shopName']?.toString() ?? 'Shop',
      branchId: asInt(json['branchId']),
      gstId: asInt(json['gstId'] ?? gst['gstId']),
      addressId: asInt(json['addressId']),
      gstNumber: (gst['gstNumber'] ?? json['gstNumber'] ?? '').toString(),
      panNumber: (gst['panNumber'] ?? json['panNumber'] ?? '').toString(),
      aadharNumber: (gst['aadharNumber'] ?? json['aadharNumber'] ?? '').toString(),
    );
  }

  ShopSummary copyWith({
    String? shopName,
    String? gstNumber,
    String? panNumber,
    String? aadharNumber,
  }) {
    return ShopSummary(
      shopId: shopId,
      userId: userId,
      shopName: shopName ?? this.shopName,
      branchId: branchId,
      gstId: gstId,
      addressId: addressId,
      gstNumber: gstNumber ?? this.gstNumber,
      panNumber: panNumber ?? this.panNumber,
      aadharNumber: aadharNumber ?? this.aadharNumber,
    );
  }
}
