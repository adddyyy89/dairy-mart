import 'package:flutter/material.dart';
import '../../models/crate.dart';
import '../../models/salesman_dashboard.dart';
import '../../services/auth_service.dart';
import '../../services/crate_service.dart';
import '../../services/dashboard_service.dart';
import '../../services/session_manager.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../widgets/app_bottom_nav.dart';
import '../../widgets/stat_card.dart';
import '../auth/login_screen.dart';
import 'salesman_activity_orders_screen.dart';
import 'salesman_crates_screen.dart';
import 'salesman_delivery_pending_screen.dart';
import 'salesman_ledger_dashboard_screen.dart';
import 'salesman_create_order_screen.dart';

/// Flutter port of res/layout/activity_salesman_dashboard_2.xml, updated to
/// match the real GET /salesman/dashboard/get/{userId} response:
/// - Profile header showing the salesman's real name + phone number
///   (the "current sector" / "in progress" / progress-bar / estimated-finish
///   hero card is gone - that was placeholder data with no backing field).
/// - 2x2 stat grid: Wallet Balance, Orders Placed, Crates Assigned (all from
///   the JSON), and Engaged Crates (from CrateService - the dashboard
///   endpoint itself has no engaged/received/returned breakdown).
/// - A real recent-transactions list: retailer name, credit/debit amount,
///   and date, straight from `recenttransactions.myArrayList`.
class SalesmanDashboardScreen extends StatefulWidget {
  const SalesmanDashboardScreen({super.key});

  @override
  State<SalesmanDashboardScreen> createState() =>
      _SalesmanDashboardScreenState();
}

class _SalesmanDashboardScreenState extends State<SalesmanDashboardScreen> {
  bool _fabExpanded = false;
  bool _isLoading = true;
  SalesmanDashboardSummary? _summary;
  CrateRecord? _crateRecord;

  @override
  void initState() {
    super.initState();
    _loadDashboard();
    WidgetsBinding.instance.addPostFrameCallback((_) => _showStartupNoticeIfAny());
  }

  void _showStartupNoticeIfAny() {
    final notice = SessionManager.instance.consumeStartupNotice();
    if (notice != null && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(notice), duration: const Duration(seconds: 4)),
      );
    }
  }

  Future<void> _logout() async {
    await AuthService.instance.logout();
    if (!mounted) return;
    Navigator.of(context).pushAndRemoveUntil(
      MaterialPageRoute(builder: (_) => const LoginScreen()),
      (route) => false,
    );
  }

  Future<void> _loadDashboard() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    setState(() => _isLoading = true);
    try {
      final summary =
          await DashboardService.instance.getSalesmanDashboard(session.userId);
      // The dashboard endpoint only returns `cratesassigned` - "engaged"
      // (handed out but not yet returned) comes from the crate endpoint,
      // same one the Crates screen uses.
      CrateRecord? crateRecord;
      try {
        crateRecord = await CrateService.instance.getCratesForUser(session.userId);
      } catch (_) {
        crateRecord = null;
      }
      if (mounted) {
        setState(() {
          _summary = summary;
          _crateRecord = crateRecord;
        });
      }
    } catch (_) {
      // real app: surface retry state
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  void _onNavTap(int index) {
    switch (index) {
      case 0:
        break;
      case 1:
        Navigator.push(context,
            MaterialPageRoute(builder: (_) => const SalesmanActivityOrdersScreen()));
        break;
      case 2:
        Navigator.push(context,
            MaterialPageRoute(builder: (_) => const SalesmanDeliveryPendingScreen()));
        break;
      case 3:
        Navigator.push(
            context, MaterialPageRoute(builder: (_) => const SalesmanCratesScreen()));
        break;
      case 4:
        Navigator.push(context,
            MaterialPageRoute(builder: (_) => const SalesmanLedgerDashboardScreen()));
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    final summary = _summary;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Sales Dashboard'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Logout',
            onPressed: _logout,
          ),
        ],
      ),
      bottomNavigationBar: AppBottomNav(currentIndex: 0, onTap: _onNavTap),
      floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
      floatingActionButton: _buildFab(context),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadDashboard,
              child: ListView(
                padding: const EdgeInsets.fromLTRB(16, 0, 16, 96),
                children: [
                  _ProfileHeaderCard(
                    name: summary?.salesmanName.isNotEmpty == true
                        ? summary!.salesmanName
                        : 'Salesman',
                    phoneNumber: summary?.salesmanPhoneNumber ?? '',
                  ),
                  const SizedBox(height: 16),
                  StatCardGrid(cards: [
                    StatCard(
                      icon: Icons.account_balance_wallet_outlined,
                      label: 'Wallet Balance',
                      value: formatCurrency(summary?.walletBalance ?? 0),
                    ),
                    StatCard(
                      icon: Icons.receipt_long_outlined,
                      label: 'Orders Placed',
                      value: '${summary?.ordersPlaced ?? 0}',
                    ),
                    StatCard(
                      icon: Icons.inventory_2_outlined,
                      label: 'Crates Assigned',
                      value: '${summary?.cratesAssigned ?? 0}',
                    ),
                    StatCard(
                      icon: Icons.assignment_return_outlined,
                      label: 'Engaged Crates',
                      value: _crateRecord != null ? '${_crateRecord!.engaged}' : '-',
                    ),
                  ]),
                  const SizedBox(height: 16),
                  _RecentTransactionsCard(
                      transactions: summary?.recentTransactions ?? const []),
                ],
              ),
            ),
    );
  }

  Widget _buildFab(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.end,
      children: [
        if (_fabExpanded) ...[
          _FabMenuEntry(
            label: 'Ledger',
            icon: Icons.account_balance_wallet_outlined,
            onTap: () => Navigator.push(context,
                MaterialPageRoute(builder: (_) => const SalesmanLedgerDashboardScreen())),
          ),
          const SizedBox(height: 12),
          _FabMenuEntry(
            label: 'Inventory / Crates',
            icon: Icons.inventory_2_outlined,
            onTap: () => Navigator.push(
                context, MaterialPageRoute(builder: (_) => const SalesmanCratesScreen())),
          ),
          const SizedBox(height: 12),
          _FabMenuEntry(
            label: 'Order History',
            icon: Icons.history_rounded,
            onTap: () => Navigator.push(context,
                MaterialPageRoute(builder: (_) => const SalesmanActivityOrdersScreen())),
          ),
          const SizedBox(height: 12),
          _FabMenuEntry(
            label: 'Create Order',
            icon: Icons.add_shopping_cart_outlined,
            onTap: () => Navigator.push(context,
                MaterialPageRoute(builder: (_) => const SalesmanCreateOrderScreen())),
          ),
          const SizedBox(height: 16),
        ],
        FloatingActionButton(
          backgroundColor: AppColors.primary,
          onPressed: () => setState(() => _fabExpanded = !_fabExpanded),
          child: Icon(_fabExpanded ? Icons.close : Icons.add),
        ),
      ],
    );
  }
}

class _ProfileHeaderCard extends StatelessWidget {
  final String name;
  final String phoneNumber;

  const _ProfileHeaderCard({required this.name, required this.phoneNumber});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: AppColors.primary,
        borderRadius: BorderRadius.circular(AppRadius.lg),
      ),
      child: Row(
        children: [
          const CircleAvatar(
            radius: 28,
            backgroundColor: Colors.white24,
            child: Icon(Icons.person, color: Colors.white, size: 28),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  name,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
                const SizedBox(height: 4),
                if (phoneNumber.isNotEmpty)
                  Row(
                    children: [
                      const Icon(Icons.call, color: Colors.white70, size: 14),
                      const SizedBox(width: 6),
                      Text(phoneNumber,
                          style: const TextStyle(color: Colors.white70, fontSize: 13)),
                    ],
                  ),
                const SizedBox(height: 2),
                Row(
                  children: [
                    const Icon(Icons.local_shipping_outlined,
                        color: Colors.white70, size: 14),
                    const SizedBox(width: 6),
                    // TODO: no vehicle field exists anywhere in the
                    // /salesman/dashboard/get/{id} response - wire this up
                    // once the backend exposes a vehicle assignment (e.g. on
                    // the User/Salesman record or a dedicated endpoint).
                    const Text('Vehicle: Not assigned',
                        style: TextStyle(color: Colors.white70, fontSize: 13)),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _RecentTransactionsCard extends StatelessWidget {
  final List<RecentTransaction> transactions;
  const _RecentTransactionsCard({required this.transactions});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('Recent Transactions',
                    style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                TextButton(
                  onPressed: () {},
                  child: const Text('View All'),
                ),
              ],
            ),
            const Divider(height: 1),
            if (transactions.isEmpty)
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 24),
                child: Center(
                  child: Text('No recent transactions yet.',
                      style: TextStyle(color: AppColors.textMuted)),
                ),
              )
            else
              ...transactions.map((t) => _TransactionTile(transaction: t)),
          ],
        ),
      ),
    );
  }
}

class _TransactionTile extends StatelessWidget {
  final RecentTransaction transaction;
  const _TransactionTile({required this.transaction});

  @override
  Widget build(BuildContext context) {
    final color = transaction.isCredit ? AppColors.success : AppColors.danger;
    final sign = transaction.isCredit ? '+' : '-';
    final date = transaction.date;
    final dateLabel =
        '${date.day.toString().padLeft(2, '0')}/${date.month.toString().padLeft(2, '0')}/${date.year}';

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 10),
      child: Row(
        children: [
          CircleAvatar(
            backgroundColor: color.withValues(alpha: 0.12),
            child: Icon(
              transaction.isCredit ? Icons.arrow_downward : Icons.arrow_upward,
              color: color,
              size: 18,
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(transaction.retailerName,
                    style: const TextStyle(fontWeight: FontWeight.w600)),
                Text(dateLabel,
                    style: const TextStyle(fontSize: 12, color: AppColors.textSecondary)),
              ],
            ),
          ),
          Text(
            '$sign${formatCurrency(transaction.amount)}',
            style: TextStyle(fontWeight: FontWeight.bold, color: color),
          ),
        ],
      ),
    );
  }
}

class _FabMenuEntry extends StatelessWidget {
  final String label;
  final IconData icon;
  final VoidCallback onTap;

  const _FabMenuEntry({required this.label, required this.icon, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
          decoration: BoxDecoration(
            color: AppColors.primary,
            borderRadius: BorderRadius.circular(8),
          ),
          child: Text(label,
              style: const TextStyle(
                  color: Colors.white, fontSize: 14, fontWeight: FontWeight.bold)),
        ),
        const SizedBox(width: 12),
        FloatingActionButton.small(
          heroTag: label,
          backgroundColor: AppColors.primary,
          onPressed: onTap,
          child: Icon(icon, color: Colors.white),
        ),
      ],
    );
  }
}