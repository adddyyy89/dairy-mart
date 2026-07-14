import 'package:flutter/material.dart';
import '../../models/order.dart';
import '../../services/order_service.dart';
import '../../services/session_manager.dart';
import '../../services/user_service.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../widgets/app_bottom_nav.dart';
import '../../widgets/status_chip.dart';
import 'salesman_dashboard_screen.dart';
import 'salesman_crates_screen.dart';
import 'salesman_delivery_pending_screen.dart';
import 'salesman_ledger_dashboard_screen.dart';
import 'order_details_screen.dart';

/// Port of res/layout/activity_salesman_activity_orders.xml:
/// profile row + notification icon, "TODAY'S SCHEDULE / Order Management"
/// header, active-orders/total-crates stat row, search bar, and a scrolling
/// list of order cards. Adds a Today / History segmented toggle and a
/// status-change action per order, matching the described salesman workflow
/// (view today's + historical orders, change order status).
class SalesmanActivityOrdersScreen extends StatefulWidget {
  const SalesmanActivityOrdersScreen({super.key});

  @override
  State<SalesmanActivityOrdersScreen> createState() =>
      _SalesmanActivityOrdersScreenState();
}

class _SalesmanActivityOrdersScreenState
    extends State<SalesmanActivityOrdersScreen> {
  bool _showTodayOnly = true;
  bool _isLoading = true;
  String _query = '';
  List<RetailOrder> _orders = [];
  String _salesmanName = '';

  @override
  void initState() {
    super.initState();
    _loadOrders();
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

  Future<void> _loadOrders() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    setState(() => _isLoading = true);
    try {
      final orders =
          await OrderService.instance.getOrdersForSalesman(session.userId);
      if (mounted) setState(() => _orders = orders);
    } catch (_) {
      // In production surface a snackbar / retry button here.
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  List<RetailOrder> get _filteredOrders {
    final now = DateTime.now();
    return _orders.where((order) {
      final matchesDate = !_showTodayOnly ||
          (order.orderDate.year == now.year &&
              order.orderDate.month == now.month &&
              order.orderDate.day == now.day);
      final matchesQuery = _query.isEmpty ||
          (order.retailerShopName ?? '')
              .toLowerCase()
              .contains(_query.toLowerCase()) ||
          order.orderId.toString().contains(_query);
      return matchesDate && matchesQuery;
    }).toList();
  }

  Future<void> _changeStatus(RetailOrder order) async {
    // Matches public.orderstatus exactly (statusid -> statusdesc):
    // 1=NEW, 2=CONFIRMED, 3=REJECTED, 4=DISPATCHED, 5=DELIVERED,
    // 6=RETURNED, 7=CANCELLED. These are NOT arbitrary - confirmed against
    // the actual DB table, since guessing here previously sent the wrong
    // id (e.g. "Delivered" was sending 3, which is really REJECTED).
    const statusOptions = <String, int>{
      'NEW': 1,
      'CONFIRMED': 2,
      'REJECTED': 3,
      'DISPATCHED': 4,
      'DELIVERED': 5,
      'RETURNED': 6,
      'CANCELLED': 7,
    };

    final selected = await showModalBottomSheet<int>(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(AppRadius.lg)),
      ),
      builder: (context) => SafeArea(
        child: ConstrainedBox(
          // Caps the sheet's height so a longer status list (7 entries)
          // scrolls internally instead of overflowing past the bottom of
          // the screen - isScrollControlled above lets it actually grow
          // this tall in the first place.
          constraints:
              BoxConstraints(maxHeight: MediaQuery.of(context).size.height * 0.75),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Padding(
                padding: EdgeInsets.all(16),
                child: Text('Update Order Status',
                    style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              ),
              Flexible(
                child: SingleChildScrollView(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: statusOptions.entries
                        .map(
                          (entry) => ListTile(
                            leading: Icon(Icons.circle,
                                size: 12, color: AppColors.statusColor(entry.key)),
                            title: Text(entry.key),
                            onTap: () => Navigator.pop(context, entry.value),
                          ),
                        )
                        .toList(),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );

    if (selected == null) return;

    final statusLabel =
        statusOptions.entries.firstWhere((e) => e.value == selected).key;

    try {
      await OrderService.instance.updateOrderStatus(
        order: order,
        newStatusId: selected,
      );
      // Reflect the change immediately in the list without waiting on a
      // re-fetch, then refresh from the server in the background to stay
      // in sync with anything else the backend recalculates on update.
      if (mounted) {
        setState(() {
          _orders = _orders
              .map((o) => o.orderId == order.orderId
                  ? o.withStatus(statusId: selected, statusDescription: statusLabel)
                  : o)
              .toList();
        });
      }
      _loadOrders();
    } catch (_) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Could not update order status.')),
        );
      }
    }
  }

  void _onNavTap(int index) {
    final Widget? destination = switch (index) {
      0 => const SalesmanDashboardScreen(),
      2 => const SalesmanDeliveryPendingScreen(),
      3 => const SalesmanCratesScreen(),
      4 => const SalesmanLedgerDashboardScreen(),
      _ => null,
    };
    if (destination != null) {
      Navigator.pushReplacement(
          context, MaterialPageRoute(builder: (_) => destination));
    }
  }

  @override
  Widget build(BuildContext context) {
    final filtered = _filteredOrders;
    final activeCount =
        _orders.where((o) => o.statusDescription != 'DELIVERED').length;
    final totalCrates = _orders.fold<int>(0, (sum, o) => sum + o.totalUnits);

    return Scaffold(
      bottomNavigationBar: AppBottomNav(currentIndex: 1, onTap: _onNavTap),
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              flex: 4,
              child: SingleChildScrollView(
                child: Padding(
                  padding: const EdgeInsets.all(24),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          const CircleAvatar(
                            radius: 18,
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
                      const SizedBox(height: 24),
                      const Text('TODAY\'S SCHEDULE',
                          style: TextStyle(
                              color: AppColors.primary,
                              fontSize: 12,
                              fontWeight: FontWeight.w600,
                              letterSpacing: 1)),
                      const SizedBox(height: 4),
                      const Text('Order Management',
                          style: TextStyle(
                              color: AppColors.textPrimary,
                              fontSize: 28,
                              fontWeight: FontWeight.bold)),
                      const SizedBox(height: 20),
                      Row(
                        children: [
                          Expanded(
                            child: _StatPill(
                              value: '$activeCount',
                              label: 'ACTIVE ORDERS',
                              background: AppColors.primaryLight,
                              valueColor: AppColors.primary,
                            ),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: _StatPill(
                              value: '$totalCrates',
                              label: 'TOTAL UNITS',
                              background: AppColors.surfaceMuted,
                              valueColor: AppColors.textPrimary,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 20),
                      Row(
                        children: [
                          Expanded(
                            child: TextField(
                              decoration: const InputDecoration(
                                hintText: 'Search...',
                                prefixIcon: Icon(Icons.search, size: 20),
                                filled: true,
                                fillColor: AppColors.surfaceMuted,
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.all(Radius.circular(24)),
                                  borderSide: BorderSide.none,
                                ),
                              ),
                              onChanged: (v) => setState(() => _query = v),
                            ),
                          ),
                          const SizedBox(width: 12),
                          ToggleButtons(
                            borderRadius: BorderRadius.circular(24),
                            isSelected: [_showTodayOnly, !_showTodayOnly],
                            onPressed: (i) => setState(() => _showTodayOnly = i == 0),
                            children: const [
                              Padding(
                                  padding: EdgeInsets.symmetric(horizontal: 12),
                                  child: Text('Today')),
                              Padding(
                                  padding: EdgeInsets.symmetric(horizontal: 12),
                                  child: Text('History')),
                            ],
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ),
            Expanded(
              flex: 6,
              child: _isLoading
                  ? const Center(child: CircularProgressIndicator())
                  : filtered.isEmpty
                      ? const Center(child: Text('No orders found.'))
                      : RefreshIndicator(
                          onRefresh: _loadOrders,
                          child: ListView.separated(
                            padding: const EdgeInsets.fromLTRB(24, 0, 24, 24),
                            itemCount: filtered.length,
                            separatorBuilder: (_, __) => const SizedBox(height: 16),
                            itemBuilder: (context, i) => _OrderCard(
                                order: filtered[i],
                                onChangeStatus: () => _changeStatus(filtered[i])),
                          ),
                        ),
            ),
          ],
        ),
      ),
    );
  }
}

class _StatPill extends StatelessWidget {
  final String value;
  final String label;
  final Color background;
  final Color valueColor;

  const _StatPill({
    required this.value,
    required this.label,
    required this.background,
    required this.valueColor,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: background,
        borderRadius: BorderRadius.circular(AppRadius.lg),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(value,
              style: TextStyle(
                  fontSize: 28, fontWeight: FontWeight.bold, color: valueColor)),
          const SizedBox(height: 4),
          Text(label,
              style: TextStyle(fontSize: 11, color: valueColor, letterSpacing: 0.5)),
        ],
      ),
    );
  }
}

class _OrderCard extends StatelessWidget {
  final RetailOrder order;
  final VoidCallback onChangeStatus;

  const _OrderCard({
    required this.order,
    required this.onChangeStatus,
  });

  String get _dateLabel {
    final d = order.orderDate;
    return '${d.day.toString().padLeft(2, '0')}/${d.month.toString().padLeft(2, '0')}/${d.year}';
  }

  @override
  Widget build(BuildContext context) {
    return InkWell(
      borderRadius: BorderRadius.circular(AppRadius.md),
      onTap: () => Navigator.push(
        context,
        MaterialPageRoute(builder: (_) => OrderDetailsScreen(order: order)),
      ),
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        if (order.retailerShopName != null)
                          Text(
                            order.retailerShopName!,
                            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                          ),
                        Text(
                          'Order #${order.orderId}',
                          style: TextStyle(
                            fontWeight:
                                order.retailerShopName == null ? FontWeight.bold : FontWeight.w500,
                            fontSize: order.retailerShopName == null ? 15 : 12,
                            color: order.retailerShopName == null
                                ? AppColors.textPrimary
                                : AppColors.textSecondary,
                          ),
                        ),
                      ],
                    ),
                  ),
                  StatusChip(label: order.statusDescription),
                ],
              ),
              const SizedBox(height: 8),
              Text(
                _dateLabel,
                style: const TextStyle(color: AppColors.textSecondary, fontSize: 13),
              ),
              const SizedBox(height: 4),
              Text(
                '${order.orderDetails.length} items • ${order.totalUnits} units',
                style: const TextStyle(color: AppColors.textSecondary, fontSize: 13),
              ),
              const SizedBox(height: 4),
              Text(
                formatCurrency(order.totalAmount),
                style: const TextStyle(
                    fontWeight: FontWeight.bold, color: AppColors.textPrimary),
              ),
              const SizedBox(height: 12),
              Align(
                alignment: Alignment.centerRight,
                child: OutlinedButton(
                  onPressed: onChangeStatus,
                  child: const Text('Change Status'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}