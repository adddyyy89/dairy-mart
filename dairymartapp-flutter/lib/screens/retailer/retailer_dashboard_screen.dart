import 'package:flutter/material.dart';
import '../../services/auth_service.dart';
import '../../services/dashboard_service.dart';
import '../../services/session_manager.dart';
import '../../services/user_service.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../widgets/load_error.dart';
import '../../widgets/retailer_bottom_nav.dart';
import '../../widgets/stat_card.dart';
import '../auth/login_screen.dart';
import '../profile_screen.dart';
import 'retailer_catalog_screen.dart';
import 'retailer_ledger_screen.dart';
import 'retailer_orders_screen.dart';

/// Port of res/layout/activity_retailer_dashboard.xml, re-themed onto the
/// same blue design system used elsewhere (the legacy layout used an
/// inconsistent olive/green palette — DESIGN.md's primary #1A73E8 wins here
/// for a cohesive app). Keeps the same content: welcome header, Balance +
/// Orders Placed + Crates stat cards, recent transactions list, bottom nav.
class RetailerDashboardScreen extends StatefulWidget {
  const RetailerDashboardScreen({super.key});

  @override
  State<RetailerDashboardScreen> createState() => _RetailerDashboardScreenState();
}

class _RetailerDashboardScreenState extends State<RetailerDashboardScreen> {
  bool _isLoading = true;
  String? _error;
  DashboardStats? _stats;
  String _firstName = '';

  @override
  void initState() {
    super.initState();
    _load();
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

  Future<void> _load() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final stats =
          await DashboardService.instance.getRetailerDashboard(session.userId);
      final user = await UserService.instance.getUser(session.userId);
      if (mounted) {
        setState(() {
          _stats = stats;
          _firstName = user.firstName;
        });
      }
    } catch (_) {
      if (mounted) setState(() => _error = 'Could not load dashboard.');
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  Future<void> _openCatalog() async {
    await Navigator.push(context,
        MaterialPageRoute(builder: (_) => const RetailerCatalogScreen()));
    if (mounted) await _load();
  }

  void _onNavTap(int index) {
    switch (index) {
      case 0:
        break;
      case 1:
        Navigator.push(context,
            MaterialPageRoute(builder: (_) => const RetailerOrdersScreen()));
        break;
      case 2:
        _openCatalog();
        break;
      case 3:
        Navigator.push(context,
            MaterialPageRoute(builder: (_) => const RetailerLedgerScreen()));
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    final stats = _stats;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Dairy Mart'),
        actions: [
          IconButton(
            icon: const Icon(Icons.person_outline),
            onPressed: () => Navigator.push(
                context, MaterialPageRoute(builder: (_) => const ProfileScreen())),
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Logout',
            onPressed: _logout,
          ),
        ],
      ),
      bottomNavigationBar: RetailerBottomNav(currentIndex: 0, onTap: _onNavTap),
      floatingActionButton: FloatingActionButton.extended(
        backgroundColor: AppColors.primary,
        icon: const Icon(Icons.add_shopping_cart),
        label: const Text('New Order'),
        onPressed: _openCatalog,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? LoadError(message: _error!, onRetry: _load)
              : RefreshIndicator(
              onRefresh: _load,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  Text(
                    'Welcome back, ${_firstName.isEmpty ? 'there' : _firstName}',
                    style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 16),
                  StatCardGrid(cards: [
                    StatCard(
                      icon: Icons.account_balance_wallet_outlined,
                      label: 'Balance',
                      value: formatCurrency(stats?.balance ?? 0),
                      valueColor: (stats?.balance ?? 0) < 0 ? AppColors.danger : null,
                    ),
                    StatCard(
                      icon: Icons.receipt_long_outlined,
                      label: 'Orders Placed',
                      value: '${stats?.ordersPlaced ?? 0}',
                    ),
                    StatCard(
                      icon: Icons.inventory_2_outlined,
                      label: 'Crates Assigned',
                      value: '${stats?.cratesAssigned ?? 0}',
                    ),
                    StatCard(
                      icon: Icons.assignment_return_outlined,
                      label: 'Engaged Crates',
                      value: '${stats?.cratesEngaged ?? 0}',
                    ),
                  ]),
                  const SizedBox(height: 24),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text('Recent Transactions',
                          style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                      TextButton(
                        onPressed: () => Navigator.push(context,
                            MaterialPageRoute(builder: (_) => const RetailerLedgerScreen())),
                        child: const Text('View All'),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  if ((stats?.recentTransactions ?? []).isEmpty)
                    const Card(
                      child: Padding(
                        padding: EdgeInsets.symmetric(vertical: 24),
                        child: Center(
                          child: Text('No recent transactions.',
                              style: TextStyle(color: AppColors.textMuted)),
                        ),
                      ),
                    )
                  else
                    ...stats!.recentTransactions.map((t) {
                      final color =
                          t.isCredit ? AppColors.success : AppColors.danger;
                      return Card(
                        child: ListTile(
                          title: Text(
                            '${t.isCredit ? '+' : '-'}${formatCurrency(t.amount)}',
                            style: TextStyle(
                                color: color, fontWeight: FontWeight.bold),
                          ),
                          subtitle: Text(
                              '${t.paymentTypeDesc ?? 'Payment'}  ·  ${t.createdOn.day}/${t.createdOn.month}/${t.createdOn.year}'),
                        ),
                      );
                    }),
                ],
              ),
            ),
    );
  }
}