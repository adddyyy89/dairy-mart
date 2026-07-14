import 'package:flutter/material.dart';
import '../../models/order.dart';
import '../../services/order_service.dart';
import '../../services/session_manager.dart';
import '../../services/user_service.dart';
import '../../theme/app_theme.dart';
import '../../widgets/app_bottom_nav.dart';
import '../../widgets/status_chip.dart';
import 'salesman_activity_orders_screen.dart';
import 'salesman_crates_screen.dart';
import 'salesman_dashboard_screen.dart';
import 'salesman_ledger_dashboard_screen.dart';

/// Combines res/layout/activity_salesman_delivery_pending.xml and
/// activity_salesman_delivery_delivered.xml into one screen with a
/// Pending/Delivered segmented toggle (same pill-tab pattern from both
/// original layouts), plus a "Mark Delivered" action per pending order.
class SalesmanDeliveryPendingScreen extends StatefulWidget {
  const SalesmanDeliveryPendingScreen({super.key});

  @override
  State<SalesmanDeliveryPendingScreen> createState() =>
      _SalesmanDeliveryPendingScreenState();
}

class _SalesmanDeliveryPendingScreenState
    extends State<SalesmanDeliveryPendingScreen> {
  bool _showPending = true;
  bool _isLoading = true;
  List<RetailOrder> _orders = [];
  String _salesmanName = '';

  @override
  void initState() {
    super.initState();
    _load();
    _loadSalesmanName();
  }

  Future<void> _loadSalesmanName() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    try {
      final user = await UserService.instance.getUser(session.userId);
      if (mounted) setState(() => _salesmanName = user.displayName);
    } catch (_) {
      // Header falls back to a generic label if this fails.
    }
  }

  Future<void> _load() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    setState(() => _isLoading = true);
    try {
      final orders =
          await OrderService.instance.getOrdersForSalesman(session.userId);
      if (mounted) setState(() => _orders = orders);
    } catch (_) {
      // real app: show retry state
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  List<RetailOrder> get _filtered => _orders
      .where((o) => _showPending
          ? o.statusDescription != 'DELIVERED'
          : o.statusDescription == 'DELIVERED')
      .toList();

  Future<void> _markDelivered(RetailOrder order) async {
    try {
      // 5 = DELIVERED in public.orderstatus (confirmed against the DB
      // table - NOT 3, which is actually REJECTED).
      await OrderService.instance.updateOrderStatus(order: order, newStatusId: 5);
      _load();
    } catch (_) {
      if (mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(const SnackBar(content: Text('Could not mark delivered.')));
      }
    }
  }

  void _onNavTap(int index) {
    final Widget? destination = switch (index) {
      0 => const SalesmanDashboardScreen(),
      1 => const SalesmanActivityOrdersScreen(),
      3 => const SalesmanCratesScreen(),
      4 => const SalesmanLedgerDashboardScreen(),
      _ => null,
    };
    if (destination != null) {
      Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => destination));
    }
  }

  @override
  Widget build(BuildContext context) {
    final filtered = _filtered;

    return Scaffold(
      bottomNavigationBar: AppBottomNav(currentIndex: 2, onTap: _onNavTap),
      body: SafeArea(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(24, 24, 24, 8),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      const CircleAvatar(
                        radius: 20,
                        backgroundColor: AppColors.surfaceMuted,
                        child: Icon(Icons.person, color: AppColors.textMuted),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Text(
                            _salesmanName.isNotEmpty ? _salesmanName : 'Salesman',
                            style: const TextStyle(
                                color: AppColors.primary,
                                fontSize: 18,
                                fontWeight: FontWeight.bold)),
                      ),
                      const Icon(Icons.notifications_none_rounded,
                          color: AppColors.primary),
                    ],
                  ),
                  const SizedBox(height: 20),
                  Container(
                    height: 48,
                    padding: const EdgeInsets.all(4),
                    decoration: BoxDecoration(
                      color: AppColors.surfaceMuted,
                      borderRadius: BorderRadius.circular(24),
                    ),
                    child: Row(
                      children: [
                        Expanded(
                          child: _SegmentButton(
                            label: 'Pending',
                            selected: _showPending,
                            onTap: () => setState(() => _showPending = true),
                          ),
                        ),
                        Expanded(
                          child: _SegmentButton(
                            label: 'Delivered',
                            selected: !_showPending,
                            onTap: () => setState(() => _showPending = false),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            Expanded(
              child: _isLoading
                  ? const Center(child: CircularProgressIndicator())
                  : filtered.isEmpty
                      ? Center(
                          child: Text(
                            _showPending ? 'No pending deliveries.' : 'No deliveries yet.',
                            style: const TextStyle(color: AppColors.textMuted),
                          ),
                        )
                      : RefreshIndicator(
                          onRefresh: _load,
                          child: ListView.separated(
                            padding: const EdgeInsets.fromLTRB(24, 8, 24, 24),
                            itemCount: filtered.length,
                            separatorBuilder: (_, __) => const SizedBox(height: 12),
                            itemBuilder: (context, i) {
                              final order = filtered[i];
                              return Card(
                                child: ListTile(
                                  title: Text(order.retailerShopName ??
                                      'Order #${order.orderId}'),
                                  subtitle: Text(
                                      '${order.orderDetails.length} items • ${order.totalUnits} units'),
                                  trailing: _showPending
                                      ? FilledButton(
                                          onPressed: () => _markDelivered(order),
                                          child: const Text('Mark Delivered'),
                                        )
                                      : StatusChip(label: order.statusDescription),
                                ),
                              );
                            },
                          ),
                        ),
            ),
          ],
        ),
      ),
    );
  }
}

class _SegmentButton extends StatelessWidget {
  final String label;
  final bool selected;
  final VoidCallback onTap;

  const _SegmentButton({required this.label, required this.selected, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 150),
        decoration: BoxDecoration(
          color: selected ? Colors.white : Colors.transparent,
          borderRadius: BorderRadius.circular(20),
          boxShadow: selected
              ? const [BoxShadow(color: Color(0x1A000000), blurRadius: 4)]
              : null,
        ),
        alignment: Alignment.center,
        child: Text(
          label,
          style: TextStyle(
            fontWeight: FontWeight.w600,
            color: selected ? AppColors.primary : AppColors.textSecondary,
          ),
        ),
      ),
    );
  }
}