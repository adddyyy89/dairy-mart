import 'package:flutter/material.dart';
import '../../models/order.dart';
import '../../models/product.dart';
import '../../services/api_client.dart';
import '../../services/assignment_service.dart';
import '../../services/order_service.dart';
import '../../services/product_service.dart';
import '../../services/session_manager.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../widgets/load_error.dart';

class RetailerCatalogScreen extends StatefulWidget {
  const RetailerCatalogScreen({super.key});

  @override
  State<RetailerCatalogScreen> createState() => _RetailerCatalogScreenState();
}

class _RetailerCatalogScreenState extends State<RetailerCatalogScreen>
    with WidgetsBindingObserver {
  bool _isLoading = true;
  bool _isSubmitting = false;
  String? _error;
  List<Product> _products = [];
  final Map<int, int> _cart = {};
  String _query = '';

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _load();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      _load();
    }
  }

  Future<void> _load() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final products =
          await ProductService.instance.getAllProducts(forceRefresh: true);
      if (mounted) setState(() => _products = products);
    } catch (e) {
      if (mounted) {
        setState(() {
          _error = e is ApiException
              ? e.message
              : 'Could not load products. Check the API URL and retry.';
        });
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  List<Product> get _filtered {
    final q = _query.trim().toLowerCase();
    if (q.isEmpty) return _products;
    return _products
        .where((p) => p.productName.toLowerCase().contains(q))
        .toList();
  }

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
    if (session == null) {
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Please sign in again.')));
      return;
    }

    final items = _products
        .where((p) => (_cart[p.productId] ?? 0) > 0)
        .map((p) => OrderLineItem(
              productCode: p.productCode.isNotEmpty
                  ? p.productCode
                  : p.productId.toString(),
              quantity: (_cart[p.productId] ?? 0).toString(),
              unit: p.unit,
              saleRate: p.saleRate,
              purchaseRate: p.purchaseRate,
              productName: p.productName,
            ))
        .toList();

    if (items.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Add at least one product.')));
      return;
    }

    setState(() => _isSubmitting = true);
    try {
      final shop = await AssignmentService.instance
          .getOrderShopForRetailer(session.userId);
      await OrderService.instance.createOrder(
        retailerId: shop.shopId,
        branchId: shop.branchId,
        createdBy: session.userId,
        items: items,
      );
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Order placed successfully.')));
        Navigator.pop(context);
      }
    } catch (e) {
      if (mounted) {
        final message = e is ApiException ? e.message : e.toString();
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
              content: Text(
                  message.isEmpty ? 'Could not place order.' : message)),
        );
      }
    } finally {
      if (mounted) setState(() => _isSubmitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: const Text('New Order'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            tooltip: 'Refresh products',
            onPressed: _isLoading ? null : _load,
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? LoadError(message: _error!, onRetry: _load)
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
                      child: RefreshIndicator(
                        onRefresh: _load,
                        child: _filtered.isEmpty
                            ? ListView(
                                physics: const AlwaysScrollableScrollPhysics(),
                                children: [
                                  const SizedBox(height: 120),
                                  Center(
                                    child: Text(
                                      _products.isEmpty
                                          ? 'No products returned by the server.'
                                          : 'No products match your search.',
                                      style: const TextStyle(
                                          color: AppColors.textMuted),
                                      textAlign: TextAlign.center,
                                    ),
                                  ),
                                ],
                              )
                            : ListView.separated(
                                physics: const AlwaysScrollableScrollPhysics(),
                                padding:
                                    const EdgeInsets.fromLTRB(16, 0, 16, 16),
                                itemCount: _filtered.length,
                                separatorBuilder: (_, __) =>
                                    const SizedBox(height: 8),
                                itemBuilder: (context, i) {
                                  final product = _filtered[i];
                                  return _ProductRow(
                                    product: product,
                                    quantity: _cart[product.productId] ?? 0,
                                    onChanged: (delta) => setState(() {
                                      _cart[product.productId] =
                                          ((_cart[product.productId] ?? 0) +
                                                  delta)
                                              .clamp(0, 999);
                                    }),
                                  );
                                },
                              ),
                      ),
                    ),
                  ],
                ),
      bottomNavigationBar: Material(
        color: AppColors.surface,
        elevation: 8,
        child: SafeArea(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
            child: Row(
              children: [
                Expanded(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('$_cartCount items',
                          style: const TextStyle(color: AppColors.textSecondary)),
                      Text(formatCurrency(_total),
                          style: const TextStyle(
                              fontSize: 18, fontWeight: FontWeight.bold)),
                    ],
                  ),
                ),
                FilledButton(
                  onPressed:
                      _cartCount == 0 || _isSubmitting ? null : _placeOrder,
                  child: _isSubmitting
                      ? const SizedBox(
                          height: 18,
                          width: 18,
                          child: CircularProgressIndicator(
                              strokeWidth: 2, color: Colors.white))
                      : const Text('Place Order'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _ProductRow extends StatelessWidget {
  final Product product;
  final int quantity;
  final ValueChanged<int> onChanged;

  const _ProductRow({
    required this.product,
    required this.quantity,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      color: quantity > 0 ? AppColors.primaryLight : AppColors.surface,
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            Expanded(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    product.productName.isEmpty
                        ? product.productCode
                        : product.productName,
                    style: const TextStyle(fontWeight: FontWeight.w600),
                  ),
                  Text(
                    '${product.displayQuantity} • ${formatCurrency(product.saleRate)}',
                    style: const TextStyle(
                        fontSize: 12, color: AppColors.textSecondary),
                  ),
                ],
              ),
            ),
            IconButton(
              icon: const Icon(Icons.remove_circle_outline),
              color: AppColors.primary,
              onPressed: quantity > 0 ? () => onChanged(-1) : null,
            ),
            SizedBox(
              width: 24,
              child: Text('$quantity', textAlign: TextAlign.center),
            ),
            IconButton(
              icon: const Icon(Icons.add_circle_outline),
              color: AppColors.primary,
              onPressed: () => onChanged(1),
            ),
          ],
        ),
      ),
    );
  }
}
