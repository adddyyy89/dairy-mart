import 'package:flutter/material.dart';
import '../../models/ledger.dart';
import '../../services/api_client.dart';
import '../../services/api_config.dart';
import '../../services/ledger_service.dart';
import '../../services/session_manager.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../utils/json_unwrap.dart';
import '../../utils/api_error.dart';
import '../../widgets/load_error.dart';

class AdminLedgersScreen extends StatefulWidget {
  const AdminLedgersScreen({super.key});

  @override
  State<AdminLedgersScreen> createState() => _AdminLedgersScreenState();
}

class _AdminLedgersScreenState extends State<AdminLedgersScreen> {
  bool _loading = true;
  String? _error;
  List<Ledger> _ledgers = [];
  Map<String, dynamic> _credit = {};
  Map<String, dynamic> _debit = {};

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final json = await ApiClient.instance.get(ApiConfig.adminLedgers);
      final map = asMap(json);
      if (mounted) {
        setState(() {
          _ledgers = asList(map['ledger'])
              .map((e) => Ledger.fromJson(asMap(e)))
              .toList();
          _credit = asMap(map['creditmap']);
          _debit = asMap(map['debitmap']);
        });
      }
    } catch (e) {
      if (mounted) setState(() => _error = apiErrorMessage(e));
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Ledgers')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? LoadError(message: _error!, onRetry: _load)
              : RefreshIndicator(
                  onRefresh: _load,
                  child: ListView.builder(
                    padding: const EdgeInsets.all(16),
                    itemCount: _ledgers.length,
                    itemBuilder: (context, i) {
                      final ledger = _ledgers[i];
                      final key = '${ledger.ledgerId}';
                      final credit = asDouble(_credit[key]);
                      final debit = asDouble(_debit[key]);
                      return Card(
                        child: ListTile(
                          title: Text(ledger.retailerName ??
                              'Retailer #${ledger.retailerId}'),
                          subtitle: Text(
                              'Credit ${formatCurrency(credit)}  ·  Debit ${formatCurrency(debit)}'),
                          trailing: const Icon(Icons.chevron_right),
                            onTap: () => Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (_) =>
                                  LedgerDetailScreen(ledgerId: ledger.ledgerId),
                            ),
                          ),
                        ),
                      );
                    },
                  ),
                ),
    );
  }
}

class LedgerDetailScreen extends StatefulWidget {
  final int ledgerId;
  final bool canRecordPayment;
  const LedgerDetailScreen({
    super.key,
    required this.ledgerId,
    this.canRecordPayment = false,
  });

  @override
  State<LedgerDetailScreen> createState() => _LedgerDetailScreenState();
}

class _LedgerDetailScreenState extends State<LedgerDetailScreen> {
  bool _loading = true;
  bool _saving = false;
  String? _error;
  LedgerDetail? _detail;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final detail =
          await LedgerService.instance.getLedgerDetail(widget.ledgerId);
      if (mounted) setState(() => _detail = detail);
    } catch (_) {
      if (mounted) setState(() => _error = 'Could not load this ledger.');
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _recordPayment() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    final amountController = TextEditingController();
    int paymentTypeId = 1;
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setDialogState) => AlertDialog(
          title: const Text('Record collection'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Text(
                'Retailer paid this amount offline (cash / UPI). It is added to your wallet and reduces the retailer pending balance.',
                style: TextStyle(fontSize: 13),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: amountController,
                keyboardType:
                    const TextInputType.numberWithOptions(decimal: true),
                decoration: const InputDecoration(
                  labelText: 'Amount',
                  prefixText: '₹ ',
                ),
              ),
              const SizedBox(height: 8),
              DropdownButtonFormField<int>(
                value: paymentTypeId,
                decoration: const InputDecoration(labelText: 'Payment mode'),
                items: const [
                  DropdownMenuItem(value: 1, child: Text('Cash')),
                  DropdownMenuItem(value: 2, child: Text('UPI')),
                ],
                onChanged: (v) =>
                    setDialogState(() => paymentTypeId = v ?? 1),
              ),
            ],
          ),
          actions: [
            TextButton(
                onPressed: () => Navigator.pop(context, false),
                child: const Text('Cancel')),
            FilledButton(
                onPressed: () => Navigator.pop(context, true),
                child: const Text('Save')),
          ],
        ),
      ),
    );
    if (confirmed != true) return;
    final amount = double.tryParse(amountController.text.trim()) ?? 0;
    if (amount <= 0) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Enter a valid amount.')));
      }
      return;
    }
    setState(() => _saving = true);
    try {
      await LedgerService.instance.addPayment(
        ledgerId: widget.ledgerId,
        amount: amount,
        isCredit: true,
        createdBy: session.userId,
        paymentTypeId: paymentTypeId,
      );
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Collection recorded.')));
      }
      await _load();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(apiErrorMessage(e))));
      }
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final detail = _detail;
    return Scaffold(
      appBar: AppBar(title: Text(detail?.retailerName ?? 'Ledger')),
      floatingActionButton: widget.canRecordPayment && !_loading
          ? FloatingActionButton.extended(
              onPressed: _saving ? null : _recordPayment,
              icon: const Icon(Icons.payments_outlined),
              label: const Text('Record payment'),
            )
          : null,
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? LoadError(message: _error!, onRetry: _load)
              : RefreshIndicator(
                  onRefresh: _load,
                  child: ListView(
                    padding: const EdgeInsets.fromLTRB(16, 16, 16, 88),
                    children: [
                      const Text('Ledger balance',
                          style: TextStyle(color: AppColors.textSecondary)),
                      Text(formatCurrency(detail?.balance ?? 0),
                          style: TextStyle(
                              fontSize: 24,
                              fontWeight: FontWeight.bold,
                              color: (detail?.balance ?? 0) < 0
                                  ? AppColors.danger
                                  : AppColors.primary)),
                      if ((detail?.retailerAddress ?? '').isNotEmpty)
                        Padding(
                          padding: const EdgeInsets.only(top: 4),
                          child: Text(detail!.retailerAddress,
                              style: const TextStyle(
                                  color: AppColors.textSecondary)),
                        ),
                      const SizedBox(height: 16),
                      if (detail?.transactions.isEmpty ?? true)
                        const Text('No transactions.',
                            style: TextStyle(color: AppColors.textMuted))
                      else
                        ...detail!.transactions.map((t) {
                          final color =
                              t.isCredit ? AppColors.success : AppColors.danger;
                          final label = t.isCredit
                              ? 'Collection'
                              : (t.paymentTypeDesc ?? 'Order');
                          return Card(
                            child: ListTile(
                              title: Text(
                                '${t.isCredit ? '+' : '-'}${formatCurrency(t.amount)}',
                                style: TextStyle(
                                    color: color, fontWeight: FontWeight.bold),
                              ),
                              subtitle: Text(
                                  '$label  ·  ${t.createdOn.day}/${t.createdOn.month}/${t.createdOn.year}'),
                            ),
                          );
                        }),
                    ],
                  ),
                ),
    );
  }
}
