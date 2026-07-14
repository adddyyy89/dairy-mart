/// Maps to ProductDTO (dairyappserver/dto/ProductDTO.java)
/// Note: several numeric-looking fields (rates, quantity) are Strings on the
/// backend DTO, so we parse defensively.
class Product {
  final int productId;
  final String productName;
  final String? productShortName;
  final String productCode;
  final String unit;
  final String quantity;
  final double saleRate;
  final double purchaseRate;
  final double mrp;
  final String? pictureUrl;
  final bool isActive;

  Product({
    required this.productId,
    required this.productName,
    this.productShortName,
    required this.productCode,
    required this.unit,
    required this.quantity,
    required this.saleRate,
    required this.purchaseRate,
    required this.mrp,
    this.pictureUrl,
    this.isActive = true,
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    double parseNum(dynamic v) {
      if (v == null) return 0;
      if (v is num) return v.toDouble();
      return double.tryParse(v.toString()) ?? 0;
    }

    return Product(
      productId: json['productId'] ?? 0,
      productName: json['productName'] ?? '',
      productShortName: json['productShortName'],
      productCode: json['productCode']?.toString() ?? '',
      unit: json['unit']?.toString() ?? '',
      quantity: json['quantity']?.toString() ?? '',
      saleRate: parseNum(json['productSaleRate']),
      purchaseRate: parseNum(json['productPurchaseRate']),
      mrp: parseNum(json['mrp']),
      pictureUrl: json['productPictureUrl'],
      isActive: json['isActive'] ?? true,
    );
  }

  String get displayQuantity => '$quantity $unit'.trim();
}
