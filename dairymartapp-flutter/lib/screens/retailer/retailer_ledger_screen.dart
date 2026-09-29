import 'package:flutter/material.dart';
import '../../services/dashboard_service.dart';
import '../../services/session_manager.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../utils/api_error.dart';
import '../../widgets/load_error.dart';

class RetailerLedgerScreen extends StatefulWidget {
  const RetailerLedgerScreen({super.key});

  @override
  State<RetailerLedgerScreen> createState() => _RetailerLedgerScreenState();
}

class _RetailerLedgerScreenState extends State<RetailerLedgerScreen> {
  bool _loading = true;
  String? _error;
  DashboardStats? _stats;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final stats =
          await DashboardService.instance.getRetailerDashboard(session.userId);
      if (mounted) setState(() => _stats = stats);
    } catch (e) {
      if (mounted) setState(() => _error = apiErrorMessage(e));
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final stats = _stats;
    return Scaffold(
      appBar: AppBar(title: const Text('Wallet & Ledger')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? LoadError(message: _error!, onRetry: _load)
              : RefreshIndicator(
                  onRefresh: _load,
                  child: ListView(
                    padding: const EdgeInsets.all(16),
                    children: [
                      Card(
                        child: Padding(
                          padding: const EdgeInsets.all(20),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              const Text('Wallet balance',
                                  style:
                                      TextStyle(color: AppColors.textSecondary)),
                              const SizedBox(height: 4),
                              Text(
                                formatCurrency(stats?.balance ?? 0),
                                style: TextStyle(
                                    fontSize: 28,
                                    fontWeight: FontWeight.bold,
                                    color: (stats?.balance ?? 0) < 0
                                        ? AppColors.danger
                                        : AppColors.textPrimary),
                              ),
                            ],
                          ),
                        ),
                      ),
                      const SizedBox(height: 24),
                      const Text('Transaction History',
                          style: TextStyle(
                              fontSize: 16, fontWeight: FontWeight.bold)),
                      const SizedBox(height: 8),
                      if ((stats?.recentTransactions ?? []).isEmpty)
                        const Card(
                          child: Padding(
                            padding: EdgeInsets.symmetric(vertical: 32),
                            child: Center(
                              child: Text('No transactions yet.',
                                  style: TextStyle(color: AppColors.textMuted)),
                            ),
                          ),
                        )
                      else
                        ...stats!.recentTransactions.map((t) {
                          final color = t.isCredit
                              ? AppColors.success
                              : AppColors.danger;
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
