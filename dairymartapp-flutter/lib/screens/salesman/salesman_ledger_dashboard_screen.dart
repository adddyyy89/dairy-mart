import 'package:flutter/material.dart';
import '../../services/ledger_service.dart';
import '../../services/session_manager.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../utils/api_error.dart';
import '../../widgets/app_bottom_nav.dart';
import '../../widgets/load_error.dart';
import '../admin/admin_ledgers_screen.dart';
import 'salesman_activity_orders_screen.dart';
import 'salesman_crates_screen.dart';
import 'salesman_dashboard_screen.dart';
import 'salesman_delivery_pending_screen.dart';

class SalesmanLedgerDashboardScreen extends StatefulWidget {
  const SalesmanLedgerDashboardScreen({super.key});

  @override
  State<SalesmanLedgerDashboardScreen> createState() =>
      _SalesmanLedgerDashboardScreenState();
}

class _SalesmanLedgerDashboardScreenState
    extends State<SalesmanLedgerDashboardScreen> {
  bool _showOverview = true;
  bool _isLoading = true;
  String? _error;
  SalesmanLedgerDashboard? _dashboard;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final dashboard =
          await LedgerService.instance.getSalesmanDashboard(session.userId);
      if (mounted) setState(() => _dashboard = dashboard);
    } catch (e) {
      if (mounted) setState(() => _error = apiErrorMessage(e));
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  void _onNavTap(int index) {
    final Widget? destination = switch (index) {
      0 => const SalesmanDashboardScreen(),
      1 => const SalesmanActivityOrdersScreen(),
      2 => const SalesmanDeliveryPendingScreen(),
      3 => const SalesmanCratesScreen(),
      _ => null,
    };
    if (destination != null) {
      Navigator.pushReplacement(
          context, MaterialPageRoute(builder: (_) => destination));
    }
  }

  @override
  Widget build(BuildContext context) {
    final data = _dashboard;
    return Scaffold(
      appBar: AppBar(title: const Text('Ledger')),
      bottomNavigationBar: AppBottomNav(currentIndex: 4, onTap: _onNavTap),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? LoadError(message: _error!, onRetry: _load)
              : Column(
                  children: [
                    Padding(
                      padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
                      child: Row(
                        children: [
                          Expanded(
                            child: _TabButton(
                              label: 'Overview',
                              selected: _showOverview,
                              onTap: () => setState(() => _showOverview = true),
                            ),
                          ),
                          Expanded(
                            child: _TabButton(
                              label: 'Retailers',
                              selected: !_showOverview,
                              onTap: () =>
                                  setState(() => _showOverview = false),
                            ),
                          ),
                        ],
                      ),
                    ),
                    Expanded(
                      child: RefreshIndicator(
                        onRefresh: _load,
                        child: _showOverview
                            ? _OverviewTab(data: data)
                            : _RetailersTab(data: data),
                      ),
                    ),
                  ],
                ),
    );
  }
}

class _TabButton extends StatelessWidget {
  final String label;
  final bool selected;
  final VoidCallback onTap;

  const _TabButton(
      {required this.label, required this.selected, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 12),
        decoration: BoxDecoration(
          border: Border(
            bottom: BorderSide(
              color: selected ? AppColors.primary : Colors.transparent,
              width: 2,
            ),
          ),
        ),
        alignment: Alignment.center,
        child: Text(
          label,
          style: TextStyle(
            fontSize: 16,
            fontWeight: selected ? FontWeight.bold : FontWeight.normal,
            color: selected ? AppColors.primary : AppColors.textSecondary,
          ),
        ),
      ),
    );
  }
}

class _OverviewTab extends StatelessWidget {
  final SalesmanLedgerDashboard? data;
  const _OverviewTab({required this.data});

  @override
  Widget build(BuildContext context) {
    final wallet = data?.wallet ?? 0;
    final outstanding = data?.outstanding ?? 0;
    final balance = data?.balance ?? 0;
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        const Text('Balance summary',
            style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        const SizedBox(height: 12),
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('Balance',
                    style: TextStyle(color: AppColors.textSecondary)),
                Text(
                  formatCurrency(balance),
                  style: TextStyle(
                      fontSize: 22,
                      fontWeight: FontWeight.bold,
                      color: balance < 0 ? AppColors.danger : AppColors.primary),
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    children: [
                      const Text('Outstanding',
                          style: TextStyle(color: AppColors.textSecondary)),
                      Text(formatCurrency(outstanding),
                          style: const TextStyle(
                              fontSize: 18,
                              fontWeight: FontWeight.bold,
                              color: AppColors.danger)),
                    ],
                  ),
                ),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    children: [
                      const Text('Wallet',
                          style: TextStyle(color: AppColors.textSecondary)),
                      Text(formatCurrency(wallet),
                          style: const TextStyle(
                              fontSize: 18, fontWeight: FontWeight.bold)),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ],
    );
  }
}

class _RetailersTab extends StatelessWidget {
  final SalesmanLedgerDashboard? data;
  const _RetailersTab({required this.data});

  @override
  Widget build(BuildContext context) {
    final ledgers = data?.ledgers ?? const [];
    if (ledgers.isEmpty) {
      return ListView(
        children: const [
          Padding(
            padding: EdgeInsets.all(32),
            child: Center(
                child: Text('No ledgers assigned yet.',
                    style: TextStyle(color: AppColors.textMuted))),
          ),
        ],
      );
    }
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: ledgers.length,
      itemBuilder: (context, i) {
        final row = ledgers[i];
        return Card(
          margin: const EdgeInsets.only(bottom: 8),
          child: ListTile(
            leading: const CircleAvatar(
              backgroundColor: AppColors.primaryLight,
              child: Icon(Icons.storefront, color: AppColors.primary),
            ),
            title: Text(row.ledger.retailerName ??
                'Retailer #${row.ledger.retailerId}'),
            subtitle: Text(formatCurrency(row.amount)),
            trailing: const Icon(Icons.chevron_right),
            onTap: () => Navigator.push(
              context,
              MaterialPageRoute(
                builder: (_) => LedgerDetailScreen(
                    ledgerId: row.ledgerId, canRecordPayment: true),
              ),
            ),
          ),
        );
      },
    );
  }
}
