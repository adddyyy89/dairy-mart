import 'package:flutter/material.dart';
import '../../services/api_client.dart';
import '../../services/api_config.dart';
import '../../services/auth_service.dart';
import '../../theme/app_theme.dart';
import '../../widgets/stat_card.dart';
import '../auth/login_screen.dart';
import '../../services/session_manager.dart';

/// Lightweight admin overview - GET /admin/dashboard/get, with quick links
/// to user management and ledgers (/admin/users/get, /admin/ledgers/get).
/// The legacy Android app's AdminDashboardActivity is a simple button list
/// (activity_admin_dashboard.xml); this keeps the same shallow scope but in
/// the shared visual language.
class AdminDashboardScreen extends StatefulWidget {
  const AdminDashboardScreen({super.key});

  @override
  State<AdminDashboardScreen> createState() => _AdminDashboardScreenState();
}

class _AdminDashboardScreenState extends State<AdminDashboardScreen> {
  bool _isLoading = true;
  Map<String, dynamic>? _summary;

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

  Future<void> _load() async {
    setState(() => _isLoading = true);
    try {
      final json = await ApiClient.instance.get(ApiConfig.adminDashboard);
      if (mounted && json is Map<String, dynamic>) setState(() => _summary = json);
    } catch (_) {
      // real app: show retry state
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  Future<void> _logout() async {
    // Calls POST /auth/logout (Basic Auth: phone number + password) before
    // clearing the local session - see AuthService.logout for details.
    await AuthService.instance.logout();
    if (!mounted) return;
    Navigator.of(context).pushAndRemoveUntil(
      MaterialPageRoute(builder: (_) => const LoginScreen()),
      (route) => false,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Admin'),
        actions: [
          IconButton(icon: const Icon(Icons.logout), onPressed: _logout),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _load,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  StatCardGrid(cards: [
                    StatCard(
                      icon: Icons.people_outline,
                      label: 'Total Users',
                      value: '${_summary?['totalUsers'] ?? '-'}',
                    ),
                    StatCard(
                      icon: Icons.storefront_outlined,
                      label: 'Retailers',
                      value: '${_summary?['totalRetailers'] ?? '-'}',
                    ),
                    StatCard(
                      icon: Icons.badge_outlined,
                      label: 'Salesmen',
                      value: '${_summary?['totalSalesmen'] ?? '-'}',
                    ),
                    StatCard(
                      icon: Icons.receipt_long_outlined,
                      label: 'Orders Today',
                      value: '${_summary?['ordersToday'] ?? '-'}',
                    ),
                  ]),
                  const SizedBox(height: 24),
                  const Text('Manage',
                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 8),
                  Card(
                    child: ListTile(
                      leading: const Icon(Icons.people_outline, color: AppColors.primary),
                      title: const Text('Users'),
                      trailing: const Icon(Icons.chevron_right),
                      onTap: () {},
                    ),
                  ),
                  Card(
                    child: ListTile(
                      leading:
                          const Icon(Icons.account_balance_wallet_outlined, color: AppColors.primary),
                      title: const Text('Ledgers'),
                      trailing: const Icon(Icons.chevron_right),
                      onTap: () {},
                    ),
                  ),
                  Card(
                    child: ListTile(
                      leading: const Icon(Icons.inventory_2_outlined, color: AppColors.primary),
                      title: const Text('Products'),
                      trailing: const Icon(Icons.chevron_right),
                      onTap: () {},
                    ),
                  ),
                ],
              ),
            ),
    );
  }
}