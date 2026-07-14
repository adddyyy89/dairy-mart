import 'package:flutter/material.dart';
import '../../models/order.dart';
import '../../services/order_service.dart';
import '../../services/session_manager.dart';
import '../../services/user_service.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../widgets/status_chip.dart';

/// Retailer's own order history - GET /retailorder/get/retailer/{id}.
class RetailerOrdersScreen extends StatefulWidget {
  const RetailerOrdersScreen({super.key});

  @override
  State<RetailerOrdersScreen> createState() => _RetailerOrdersScreenState();
}

class _RetailerOrdersScreenState extends State<RetailerOrdersScreen> {
  bool _isLoading = true;
  List<RetailOrder> _orders = [];
  String _retailerName = '';

  @override
  void initState() {
    super.initState();
    _load();
    _loadRetailerName();
  }

  Future<void> _loadRetailerName() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    try {
      final user = await UserService.instance.getUser(session.userId);
      if (mounted) setState(() => _retailerName = user.displayName);
    } catch (_) {
      // Header falls back to a generic title if this fails.
    }
  }

  Future<void> _load() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    setState(() => _isLoading = true);
    try {
      final orders =
          await OrderService.instance.getOrdersForRetailer(session.userId);
      if (mounted) setState(() => _orders = orders);
    } catch (_) {
      // real app: show retry state
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              _retailerName.isNotEmpty ? _retailerName : 'Retailer',
              style: const TextStyle(fontSize: 13, color: AppColors.textSecondary, fontWeight: FontWeight.normal),
            ),
            const Text('My Orders'),
          ],
        ),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _orders.isEmpty
              ? const Center(
                  child: Text('No orders placed yet.', style: TextStyle(color: AppColors.textMuted)),
                )
              : RefreshIndicator(
                  onRefresh: _load,
                  child: ListView.separated(
                    padding: const EdgeInsets.all(16),
                    itemCount: _orders.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 12),
                    itemBuilder: (context, i) {
                      final order = _orders[i];
                      return Card(
                        child: Padding(
                          padding: const EdgeInsets.all(16),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Text('Order #${order.orderId}',
                                      style: const TextStyle(fontWeight: FontWeight.bold)),
                                  StatusChip(label: order.statusDescription),
                                ],
                              ),
                              const SizedBox(height: 8),
                              Text(
                                '${order.orderDate.day}/${order.orderDate.month}/${order.orderDate.year} • ${order.orderDetails.length} items',
                                style: const TextStyle(color: AppColors.textSecondary, fontSize: 13),
                              ),
                              const SizedBox(height: 4),
                              Text(formatCurrency(order.totalAmount),
                                  style: const TextStyle(fontWeight: FontWeight.bold)),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
                ),
    );
  }
}