import 'package:flutter/material.dart';
import '../../models/ledger.dart';
import '../../services/ledger_service.dart';
import '../../services/session_manager.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../widgets/app_bottom_nav.dart';
import 'salesman_activity_orders_screen.dart';
import 'salesman_crates_screen.dart';
import 'salesman_dashboard_screen.dart';
import 'salesman_delivery_pending_screen.dart';

/// Port of res/layout/activity_salesman_ledger_dashboard.xml:
/// Overview/Transactions tab row, Balance summary card, Outstanding/Wallet
/// stat pair, search field, and a scrolling transaction list.
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
  List<Ledger> _ledgers = [];

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    setState(() => _isLoading = true);
    try {
      final ledgers =
          await LedgerService.instance.getLedgersForSalesman(session.userId);
      if (mounted) setState(() => _ledgers = ledgers);
    } catch (_) {
      // real app: surface an error state / retry
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
      Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => destination));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Ledger')),
      bottomNavigationBar: AppBottomNav(currentIndex: 4, onTap: _onNavTap),
      body: Column(
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
                    label: 'Transactions',
                    selected: !_showOverview,
                    onTap: () => setState(() => _showOverview = false),
                  ),
                ),
              ],
            ),
          ),
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : RefreshIndicator(
                    onRefresh: _load,
                    child: _showOverview
                        ? _OverviewTab(ledgers: _ledgers)
                        : _TransactionsTab(ledgers: _ledgers),
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

  const _TabButton({required this.label, required this.selected, required this.onTap});

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
  final List<Ledger> ledgers;
  const _OverviewTab({required this.ledgers});

  @override
  Widget build(BuildContext context) {
    // TODO: replace placeholder totals with a real aggregation once the
    // /ledger summary endpoint response shape is confirmed.
    const balance = -2494.00;
    const outstanding = -11108.00;
    const wallet = 8614.00;

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
                const Text('Balance', style: TextStyle(color: AppColors.textSecondary)),
                Text(
                  formatCurrency(balance),
                  style: const TextStyle(
                      fontSize: 22, fontWeight: FontWeight.bold, color: AppColors.danger),
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: _SummaryTile(
                  label: 'Outstanding', value: outstanding, negative: true),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _SummaryTile(label: 'Wallet', value: wallet, negative: false),
            ),
          ],
        ),
        const SizedBox(height: 20),
        const Text('Retailer Ledgers',
            style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        const SizedBox(height: 8),
        if (ledgers.isEmpty)
          const Padding(
            padding: EdgeInsets.symmetric(vertical: 16),
            child: Text('No ledgers assigned yet.',
                style: TextStyle(color: AppColors.textMuted)),
          )
        else
          ...ledgers.map((l) => Card(
                margin: const EdgeInsets.only(bottom: 8),
                child: ListTile(
                  leading: const CircleAvatar(
                    backgroundColor: AppColors.primaryLight,
                    child: Icon(Icons.storefront, color: AppColors.primary),
                  ),
                  title: Text(l.retailerName ?? 'Retailer #${l.retailerId}'),
                  subtitle: Text(l.active ? 'Active' : 'Inactive'),
                  trailing: const Icon(Icons.chevron_right),
                ),
              )),
      ],
    );
  }
}

class _SummaryTile extends StatelessWidget {
  final String label;
  final double value;
  final bool negative;

  const _SummaryTile({required this.label, required this.value, required this.negative});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Text(label, style: const TextStyle(color: AppColors.textSecondary)),
            const SizedBox(height: 4),
            Text(
              formatCurrency(value),
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: negative ? AppColors.danger : AppColors.textPrimary,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _TransactionsTab extends StatelessWidget {
  final List<Ledger> ledgers;
  const _TransactionsTab({required this.ledgers});

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        TextField(
          decoration: const InputDecoration(
            hintText: 'Search Ledger...',
            prefixIcon: Icon(Icons.search),
          ),
        ),
        const SizedBox(height: 16),
        // Bind to LedgerService.getTransactions(ledgerId) per selected ledger
        // once a ledger is chosen from the Overview tab.
        const Padding(
          padding: EdgeInsets.symmetric(vertical: 32),
          child: Center(
            child: Text('Select a retailer from Overview to see transactions.',
                style: TextStyle(color: AppColors.textMuted)),
          ),
        ),
      ],
    );
  }
}
