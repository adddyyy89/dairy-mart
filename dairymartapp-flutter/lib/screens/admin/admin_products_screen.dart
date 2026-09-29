import 'package:flutter/material.dart';
import '../../models/product.dart';
import '../../services/product_service.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../utils/api_error.dart';
import '../../widgets/load_error.dart';

class AdminProductsScreen extends StatefulWidget {
  const AdminProductsScreen({super.key});

  @override
  State<AdminProductsScreen> createState() => _AdminProductsScreenState();
}

class _AdminProductsScreenState extends State<AdminProductsScreen> {
  bool _loading = true;
  String? _error;
  List<Product> _products = [];

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final products =
          await ProductService.instance.getAllProducts(forceRefresh: true);
      if (mounted) setState(() => _products = products);
    } catch (e) {
      if (mounted) setState(() => _error = apiErrorMessage(e));
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Products')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? LoadError(message: _error!, onRetry: _load)
              : RefreshIndicator(
                  onRefresh: _load,
                  child: ListView.separated(
                    padding: const EdgeInsets.all(16),
                    itemCount: _products.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 8),
                    itemBuilder: (context, i) {
                      final p = _products[i];
                      return Card(
                        child: ListTile(
                          title: Text(p.productName),
                          subtitle: Text(
                              '${p.productCode}  ·  ${p.displayQuantity}'),
                          trailing: Text(formatCurrency(p.saleRate),
                              style: const TextStyle(
                                  fontWeight: FontWeight.bold,
                                  color: AppColors.primary)),
                        ),
                      );
                    },
                  ),
                ),
    );
  }
}
