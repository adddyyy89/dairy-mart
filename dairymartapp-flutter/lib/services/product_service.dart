import '../models/product.dart';
import '../utils/json_unwrap.dart';
import 'api_client.dart';
import 'api_config.dart';

class ProductService {
  ProductService._();
  static final ProductService instance = ProductService._();

  List<Product>? _cache;

  Future<List<Product>> getAllProducts({bool forceRefresh = false}) async {
    if (_cache != null && !forceRefresh) return _cache!;
    dynamic json;
    try {
      json = await ApiClient.instance.get(ApiConfig.productGetAll);
    } catch (_) {
      json = await ApiClient.instance.get(ApiConfig.orderProducts);
    }
    final products = asList(json)
        .map((e) => Product.fromJson(asMap(e)))
        .where((p) => p.productName.isNotEmpty || p.productCode.isNotEmpty)
        .toList();
    _cache = products;
    return products;
  }

  /// Looks up a product by its productCode (as used on order line items),
  /// pulling from GET /product/get since order details only carry the code,
  /// not the name.
  Future<Product?> getByProductCode(String productCode) async {
    final products = await getAllProducts();
    for (final p in products) {
      if (p.productCode == productCode) return p;
    }
    return null;
  }
}
