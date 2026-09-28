import 'package:flutter/material.dart';
import '../../models/ledger.dart';
import '../../services/api_client.dart';
import '../../services/api_config.dart';
import '../../services/ledger_service.dart';
import '../../theme/app_theme.dart';
import '../../utils/currency_formatter.dart';
import '../../utils/json_unwrap.dart';
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
    } catch (_) {
      if (mounted) setState(() => _error = 'Could not load ledgers.');
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
  const LedgerDetailScreen({super.key, required this.ledgerId});

  @override
  State<LedgerDetailScreen> createState() => _LedgerDetailScreenState();
}

class _LedgerDetailScreenState extends State<LedgerDetailScreen> {
  bool _loading = true;
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

  @override
  Widget build(BuildContext context) {
    final detail = _detail;
    return Scaffold(
      appBar: AppBar(title: Text(detail?.retailerName ?? 'Ledger')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? LoadError(message: _error!, onRetry: _load)
              : RefreshIndicator(
                  onRefresh: _load,
                  child: ListView(
                    padding: const EdgeInsets.all(16),
                    children: [
                      Text(formatCurrency(detail?.balance ?? 0),
                          style: const TextStyle(
                              fontSize: 24,
                              fontWeight: FontWeight.bold,
                              color: AppColors.primary)),
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
