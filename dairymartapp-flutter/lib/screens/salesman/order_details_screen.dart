import 'package:flutter/material.dart';
import '../../models/order.dart';
import '../../services/product_service.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../widgets/status_chip.dart';

/// Order details screen - drills into a single RetailOrder.
/// For each line item, shows: product name (resolved client-side from
/// GET /product/get, since order details only carry productCode), product
/// code, quantity, unit, sale rate, and last updated - plus the order id
/// and status at the top.
class OrderDetailsScreen extends StatefulWidget {
  final RetailOrder order;
  const OrderDetailsScreen({super.key, required this.order});

  @override
  State<OrderDetailsScreen> createState() => _OrderDetailsScreenState();
}

class _OrderDetailsScreenState extends State<OrderDetailsScreen> {
  bool _isLoading = true;
  List<OrderLineItem> _items = [];

  @override
  void initState() {
    super.initState();
    _resolveProductNames();
  }

  Future<void> _resolveProductNames() async {
    setState(() => _isLoading = true);
    try {
      final products = await ProductService.instance.getAllProducts();
      // Normalize both sides (trim + uppercase) before matching - a stray
      // space or case difference between the order's productCode and the
      // catalog's productCode would otherwise silently fail to match and
      // fall back to showing the raw code instead of the product name.
      final byCode = {
        for (final p in products) p.productCode.trim().toUpperCase(): p.productName
      };
      final resolved = widget.order.orderDetails
          .map((item) =>
              item.withProductName(byCode[item.productCode.trim().toUpperCase()]))
          .toList();
      if (mounted) setState(() => _items = resolved);
    } catch (_) {
      // Fall back to showing the raw line items (product code only) if the
      // catalog fetch fails - still usable, just without the friendly name.
      if (mounted) setState(() => _items = widget.order.orderDetails);
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  double get _total =>
      _items.fold(0, (sum, item) => sum + item.lineTotal);

  @override
  Widget build(BuildContext context) {
    final order = widget.order;

    return Scaffold(
      appBar: AppBar(title: Text('Order #${order.orderId}')),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : ListView(
              padding: const EdgeInsets.all(16),
              children: [
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text('Order #${order.orderId}',
                                style: const TextStyle(
                                    fontSize: 18, fontWeight: FontWeight.bold)),
                            StatusChip(label: order.statusDescription),
                          ],
                        ),
                        const SizedBox(height: 6),
                        if (order.retailerShopName != null)
                          Text(order.retailerShopName!,
                              style: const TextStyle(color: AppColors.textSecondary)),
                        const SizedBox(height: 4),
                        Text(
                          '${order.orderDate.day.toString().padLeft(2, '0')}/${order.orderDate.month.toString().padLeft(2, '0')}/${order.orderDate.year}',
                          style: const TextStyle(color: AppColors.textSecondary, fontSize: 13),
                        ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                Text('Items (${_items.length})',
                    style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                const SizedBox(height: 8),
                ..._items.map((item) => _LineItemCard(item: item)),
                const SizedBox(height: 8),
                Card(
                  color: AppColors.primaryLight,
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text('Order Total',
                            style: TextStyle(fontWeight: FontWeight.bold)),
                        Text(
                          formatCurrency(_total),
                          style: const TextStyle(
                              fontSize: 18,
                              fontWeight: FontWeight.bold,
                              color: AppColors.primary),
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
    );
  }
}

class _LineItemCard extends StatelessWidget {
  final OrderLineItem item;
  const _LineItemCard({required this.item});

  @override
  Widget build(BuildContext context) {
    final lastUpdated = item.lastUpdated;
    final lastUpdatedLabel = lastUpdated != null
        ? '${lastUpdated.day.toString().padLeft(2, '0')}/${lastUpdated.month.toString().padLeft(2, '0')}/${lastUpdated.year}'
        : '-';

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              // Product Code is already shown as its own row below, so the
              // fallback here is just the bare code (no redundant "Product"
              // prefix) when a catalog match isn't found.
              item.productName ?? item.productCode,
              style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
            ),
            const SizedBox(height: 12),
            _DetailRow(label: 'Product Code', value: item.productCode),
            // Quantity shown on its own - Unit has its own row right below,
            // so concatenating them here just duplicated the value whenever
            // unit happened to be numeric (e.g. "10 10").
            _DetailRow(label: 'Quantity', value: item.quantity),
            _DetailRow(label: 'Unit', value: item.unit),
            _DetailRow(label: 'Sale Rate', value: formatCurrency(item.saleRate)),
            _DetailRow(label: 'Last Updated', value: lastUpdatedLabel),
          ],
        ),
      ),
    );
  }
}

class _DetailRow extends StatelessWidget {
  final String label;
  final String value;
  const _DetailRow({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: const TextStyle(color: AppColors.textSecondary, fontSize: 13)),
          Text(value, style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 13)),
        ],
      ),
    );
  }
}