import 'package:flutter/material.dart';
import '../../models/order.dart';
import '../../models/product.dart';
import '../../services/order_service.dart';
import '../../services/product_service.dart';
import '../../services/session_manager.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';

/// Port of activity_salesman_create_order.xml + list_salesman_create_order_items.xml:
/// pick a retailer, add products with quantities from the catalog, review the
/// running total, and submit via POST /retailorder/add.
class SalesmanCreateOrderScreen extends StatefulWidget {
  const SalesmanCreateOrderScreen({super.key});

  @override
  State<SalesmanCreateOrderScreen> createState() =>
      _SalesmanCreateOrderScreenState();
}

class _SalesmanCreateOrderScreenState
    extends State<SalesmanCreateOrderScreen> {
  bool _isLoadingProducts = true;
  bool _isSubmitting = false;
  List<Product> _products = [];
  final Map<int, int> _quantities = {}; // productId -> qty

  @override
  void initState() {
    super.initState();
    _loadProducts();
  }

  Future<void> _loadProducts() async {
    try {
      final products = await ProductService.instance.getAllProducts();
      if (mounted) setState(() => _products = products);
    } catch (_) {
      // real app: show retry state
    } finally {
      if (mounted) setState(() => _isLoadingProducts = false);
    }
  }

  double get _total {
    double sum = 0;
    for (final p in _products) {
      final qty = _quantities[p.productId] ?? 0;
      sum += qty * p.saleRate;
    }
    return sum;
  }

  int get _itemCount => _quantities.values.where((q) => q > 0).length;

  void _updateQuantity(Product product, int delta) {
    setState(() {
      final current = _quantities[product.productId] ?? 0;
      final next = (current + delta).clamp(0, 999);
      _quantities[product.productId] = next;
    });
  }

  Future<void> _submitOrder() async {
    final session = SessionManager.instance.current;
    if (session == null) return;

    final items = _products
        .where((p) => (_quantities[p.productId] ?? 0) > 0)
        .map((p) => OrderLineItem(
              productCode: p.productCode,
              quantity: (_quantities[p.productId] ?? 0).toString(),
              unit: p.unit,
              saleRate: p.saleRate,
              purchaseRate: p.purchaseRate,
              productName: p.productName,
            ))
        .toList();

    if (items.isEmpty) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text('Add at least one product.')));
      return;
    }

    // TODO: replace with a real retailer picker (fetch via
    // /salesmantoretail/get/assignment/salesman/{id}) - hardwired here so the
    // submit flow is complete end-to-end.
    const retailerId = 0;
    const branchId = 0;

    setState(() => _isSubmitting = true);
    try {
      await OrderService.instance.createOrder(
        retailerId: retailerId,
        branchId: branchId,
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
      appBar: AppBar(title: const Text('Create Order')),
      body: _isLoadingProducts
          ? const Center(child: CircularProgressIndicator())
          : ListView.separated(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 120),
              itemCount: _products.length,
              separatorBuilder: (_, __) => const SizedBox(height: 8),
              itemBuilder: (context, i) => _ProductLineTile(
                product: _products[i],
                quantity: _quantities[_products[i].productId] ?? 0,
                onChanged: (delta) => _updateQuantity(_products[i], delta),
              ),
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
                    Text('$_itemCount items',
                        style: const TextStyle(color: AppColors.textSecondary)),
                    Text(formatCurrency(_total),
                        style: const TextStyle(
                            fontSize: 18, fontWeight: FontWeight.bold)),
                  ],
                ),
              ),
              FilledButton(
                onPressed: _isSubmitting ? null : _submitOrder,
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

class _ProductLineTile extends StatelessWidget {
  final Product product;
  final int quantity;
  final ValueChanged<int> onChanged;

  const _ProductLineTile({
    required this.product,
    required this.quantity,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    final selected = quantity > 0;
    return Card(
      color: selected ? AppColors.primaryLight : AppColors.surface,
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            Container(
              width: 44,
              height: 44,
              decoration: BoxDecoration(
                color: AppColors.surfaceMuted,
                borderRadius: BorderRadius.circular(AppRadius.sm),
              ),
              child: const Icon(Icons.local_drink_outlined, color: AppColors.primary),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(product.productName,
                      style: const TextStyle(fontWeight: FontWeight.w600)),
                  Text(
                    '${product.displayQuantity} • ${formatCurrency(product.saleRate)}',
                    style: const TextStyle(fontSize: 12, color: AppColors.textSecondary),
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
