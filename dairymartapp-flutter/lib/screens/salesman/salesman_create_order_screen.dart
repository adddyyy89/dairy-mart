import 'package:flutter/material.dart';
import '../../models/assignment.dart';
import '../../models/order.dart';
import '../../models/product.dart';
import '../../services/assignment_service.dart';
import '../../services/order_service.dart';
import '../../services/product_service.dart';
import '../../services/session_manager.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../widgets/load_error.dart';

class SalesmanCreateOrderScreen extends StatefulWidget {
  const SalesmanCreateOrderScreen({super.key});

  @override
  State<SalesmanCreateOrderScreen> createState() =>
      _SalesmanCreateOrderScreenState();
}

class _SalesmanCreateOrderScreenState extends State<SalesmanCreateOrderScreen>
    with WidgetsBindingObserver {
  bool _isLoading = true;
  bool _isSubmitting = false;
  String? _error;
  List<Product> _products = [];
  List<SalesmanAssignment> _assignments = [];
  SalesmanAssignment? _selected;
  final Map<int, int> _quantities = {};

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
    final session = SessionManager.instance.current;
    if (session == null) return;
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final products =
          await ProductService.instance.getAllProducts(forceRefresh: true);
      final assignments =
          await AssignmentService.instance.getForSalesman(session.userId);
      if (mounted) {
        setState(() {
          _products = products;
          _assignments = assignments;
          _selected ??= assignments.isNotEmpty ? assignments.first : null;
        });
      }
    } catch (_) {
      if (mounted) setState(() => _error = 'Could not load catalog.');
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  double get _total {
    double sum = 0;
    for (final p in _products) {
      sum += (_quantities[p.productId] ?? 0) * p.saleRate;
    }
    return sum;
  }

  int get _itemCount => _quantities.values.where((q) => q > 0).length;

  void _updateQuantity(Product product, int delta) {
    setState(() {
      final current = _quantities[product.productId] ?? 0;
      _quantities[product.productId] = (current + delta).clamp(0, 999);
    });
  }

  Future<void> _submitOrder() async {
    final session = SessionManager.instance.current;
    final retailer = _selected;
    if (session == null || retailer == null) {
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Select a retailer first.')));
      return;
    }

    final items = _products
        .where((p) => (_quantities[p.productId] ?? 0) > 0)
        .map((p) => OrderLineItem(
              productCode: p.productCode.isNotEmpty
                  ? p.productCode
                  : p.productId.toString(),
              quantity: (_quantities[p.productId] ?? 0).toString(),
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
      await OrderService.instance.createOrder(
        retailerId: retailer.shopId,
        branchId: retailer.branchId,
        createdBy: session.userId,
        items: items,
      );
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Order placed successfully.')));
        Navigator.pop(context);
      }
    } catch (_) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Could not place order.')));
      }
    } finally {
      if (mounted) setState(() => _isSubmitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Create Order'),
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
                      padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
                      child: InputDecorator(
                        decoration: const InputDecoration(
                          labelText: 'Retailer',
                          border: OutlineInputBorder(),
                        ),
                        child: DropdownButtonHideUnderline(
                          child: DropdownButton<int>(
                            isExpanded: true,
                            hint: const Text('Select retailer'),
                            value: _selected?.shopId,
                            items: _assignments
                                .map((a) => DropdownMenuItem(
                                      value: a.shopId,
                                      child: Text(a.shopName),
                                    ))
                                .toList(),
                            onChanged: (id) {
                              SalesmanAssignment? next;
                              for (final a in _assignments) {
                                if (a.shopId == id) next = a;
                              }
                              setState(() => _selected = next);
                            },
                          ),
                        ),
                      ),
                    ),
                    Expanded(
                      child: RefreshIndicator(
                        onRefresh: _load,
                        child: ListView.separated(
                          physics: const AlwaysScrollableScrollPhysics(),
                          padding: const EdgeInsets.fromLTRB(16, 8, 16, 16),
                          itemCount: _products.length,
                          separatorBuilder: (_, __) => const SizedBox(height: 8),
                          itemBuilder: (context, i) => _ProductLineTile(
                            product: _products[i],
                            quantity: _quantities[_products[i].productId] ?? 0,
                            onChanged: (delta) =>
                                _updateQuantity(_products[i], delta),
                          ),
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
                  mainAxisSize: MainAxisSize.min,
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
                        child: CircularProgressIndicator(
                            strokeWidth: 2, color: Colors.white))
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
              child: const Icon(Icons.local_drink_outlined,
                  color: AppColors.primary),
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
