import 'package:flutter/material.dart';
import '../../models/order.dart';
import '../../models/product.dart';
import '../../services/order_service.dart';
import '../../services/product_service.dart';
import '../../services/session_manager.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';

/// Retailer-facing product catalog + order builder. Uses the same
/// GET /product/get catalog as the salesman's create-order screen, then
/// posts to /retailorder/add with the retailer's own id.
class RetailerCatalogScreen extends StatefulWidget {
  const RetailerCatalogScreen({super.key});

  @override
  State<RetailerCatalogScreen> createState() => _RetailerCatalogScreenState();
}

class _RetailerCatalogScreenState extends State<RetailerCatalogScreen> {
  bool _isLoading = true;
  bool _isSubmitting = false;
  List<Product> _products = [];
  final Map<int, int> _cart = {};
  String _query = '';

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    try {
      final products = await ProductService.instance.getAllProducts();
      if (mounted) setState(() => _products = products);
    } catch (_) {
      // real app: show retry state
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  List<Product> get _filtered => _query.isEmpty
      ? _products
      : _products
          .where((p) => p.productName.toLowerCase().contains(_query.toLowerCase()))
          .toList();

  double get _total {
    double sum = 0;
    for (final p in _products) {
      sum += (_cart[p.productId] ?? 0) * p.saleRate;
    }
    return sum;
  }

  int get _cartCount => _cart.values.fold(0, (a, b) => a + b);

  Future<void> _placeOrder() async {
    final session = SessionManager.instance.current;
    if (session == null) return;

    final items = _products
        .where((p) => (_cart[p.productId] ?? 0) > 0)
        .map((p) => OrderLineItem(
              productCode: p.productCode,
              quantity: (_cart[p.productId] ?? 0).toString(),
              unit: p.unit,
              saleRate: p.saleRate,
              purchaseRate: p.purchaseRate,
              productName: p.productName,
            ))
        .toList();

    if (items.isEmpty) return;

    setState(() => _isSubmitting = true);
    try {
      await OrderService.instance.createOrder(
        retailerId: session.userId,
        branchId: 0, // TODO: populate from the retailer's shop/branch profile
        createdBy: session.userId,
        items: items,
      );
      if (mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(const SnackBar(content: Text('Order placed successfully.')));
        Navigator.pop(context);
      }
    } catch (_) {
      if (mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(const SnackBar(content: Text('Could not place order.')));
      }
    } finally {
      if (mounted) setState(() => _isSubmitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('New Order')),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : Column(
              children: [
                Padding(
                  padding: const EdgeInsets.all(16),
                  child: TextField(
                    decoration: const InputDecoration(
                      hintText: 'Search products...',
                      prefixIcon: Icon(Icons.search),
                    ),
                    onChanged: (v) => setState(() => _query = v),
                  ),
                ),
                Expanded(
                  child: GridView.builder(
                    padding: const EdgeInsets.fromLTRB(16, 0, 16, 120),
                    gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 2,
                      mainAxisSpacing: 12,
                      crossAxisSpacing: 12,
                      childAspectRatio: 0.85,
                    ),
                    itemCount: _filtered.length,
                    itemBuilder: (context, i) => _CatalogCard(
                      product: _filtered[i],
                      quantity: _cart[_filtered[i].productId] ?? 0,
                      onChanged: (delta) => setState(() {
                        final id = _filtered[i].productId;
                        _cart[id] = ((_cart[id] ?? 0) + delta).clamp(0, 999);
                      }),
                    ),
                  ),
                ),
              ],
            ),
      bottomNavigationBar: SafeArea(
        child: Container(
          padding: const EdgeInsets.all(16),
          decoration: const BoxDecoration(
            color: AppColors.surface,
            boxShadow: [BoxShadow(color: Color(0x1A000000), blurRadius: 8)],
          ),
          child: Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('$_cartCount items', style: const TextStyle(color: AppColors.textSecondary)),
                    Text(formatCurrency(_total),
                        style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  ],
                ),
              ),
              FilledButton(
                onPressed: _cartCount == 0 || _isSubmitting ? null : _placeOrder,
                child: _isSubmitting
                    ? const SizedBox(
                        height: 18,
                        width: 18,
                        child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
                    : const Text('Place Order'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _CatalogCard extends StatelessWidget {
  final Product product;
  final int quantity;
  final ValueChanged<int> onChanged;

  const _CatalogCard({required this.product, required this.quantity, required this.onChanged});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              height: 64,
              width: double.infinity,
              decoration: BoxDecoration(
                color: AppColors.surfaceMuted,
                borderRadius: BorderRadius.circular(AppRadius.sm),
              ),
              child: const Icon(Icons.local_drink_outlined, color: AppColors.primary, size: 32),
            ),
            const SizedBox(height: 8),
            Text(product.productName,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13)),
            Text(product.displayQuantity,
                style: const TextStyle(fontSize: 11, color: AppColors.textSecondary)),
            const Spacer(),
            Text(formatCurrency(product.saleRate),
                style: const TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            quantity == 0
                ? SizedBox(
                    width: double.infinity,
                    child: OutlinedButton(
                      onPressed: () => onChanged(1),
                      child: const Text('Add'),
                    ),
                  )
                : Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      IconButton(
                        visualDensity: VisualDensity.compact,
                        icon: const Icon(Icons.remove_circle_outline),
                        color: AppColors.primary,
                        onPressed: () => onChanged(-1),
                      ),
                      Text('$quantity'),
                      IconButton(
                        visualDensity: VisualDensity.compact,
                        icon: const Icon(Icons.add_circle_outline),
                        color: AppColors.primary,
                        onPressed: () => onChanged(1),
                      ),
                    ],
                  ),
          ],
        ),
      ),
    );
  }
}
