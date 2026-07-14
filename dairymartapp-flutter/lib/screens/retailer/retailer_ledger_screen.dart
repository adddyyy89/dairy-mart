import 'package:flutter/material.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';

/// Retailer's ledger/wallet view - balance, outstanding dues, wallet credit,
/// and transaction history, per the requirement: "Retailer can view
/// transactions, wallet details etc."
class RetailerLedgerScreen extends StatefulWidget {
  const RetailerLedgerScreen({super.key});

  @override
  State<RetailerLedgerScreen> createState() => _RetailerLedgerScreenState();
}

class _RetailerLedgerScreenState extends State<RetailerLedgerScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Wallet & Ledger')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Card(
            child: Padding(
              padding: const EdgeInsets.all(20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Outstanding Balance',
                      style: TextStyle(color: AppColors.textSecondary)),
                  const SizedBox(height: 4),
                  // TODO: bind to LedgerService / DashboardService.getLedgerSummary
                  // once the summary endpoint response shape is confirmed
                  // against a live backend, using session?.userId.
                  const Text(
                    '₹ 0.00',
                    style: TextStyle(
                        fontSize: 28, fontWeight: FontWeight.bold, color: AppColors.textPrimary),
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
                        const Text('Wallet', style: TextStyle(color: AppColors.textSecondary)),
                        const SizedBox(height: 4),
                        Text(formatCurrency(0),
                            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
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
                        const Text('Credit Limit', style: TextStyle(color: AppColors.textSecondary)),
                        const SizedBox(height: 4),
                        Text(formatCurrency(0),
                            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 24),
          const Text('Transaction History',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          const Card(
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 32),
              child: Center(
                child: Text('No transactions yet.', style: TextStyle(color: AppColors.textMuted)),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
