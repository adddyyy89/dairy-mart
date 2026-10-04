import 'package:flutter/material.dart';
import '../../models/crate.dart';
import '../../services/assignment_service.dart';
import '../../services/crate_service.dart';
import '../../services/session_manager.dart';
import '../../theme/app_theme.dart';
import '../../utils/api_error.dart';
import '../../widgets/app_bottom_nav.dart';
import '../../widgets/load_error.dart';
import '../../widgets/stat_card.dart';
import 'salesman_activity_orders_screen.dart';
import 'salesman_dashboard_screen.dart';
import 'salesman_delivery_pending_screen.dart';
import 'salesman_ledger_dashboard_screen.dart';

class SalesmanCratesScreen extends StatefulWidget {
  const SalesmanCratesScreen({super.key});

  @override
  State<SalesmanCratesScreen> createState() => _SalesmanCratesScreenState();
}

class _SalesmanCratesScreenState extends State<SalesmanCratesScreen> {
  bool _isLoading = true;
  String? _error;
  CrateRecord? _own;
  List<CrateRecord> _stores = [];

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
      final own = await CrateService.instance.getCratesForUser(session.userId);
      final assignments =
          await AssignmentService.instance.getForSalesman(session.userId);
      final stores = <CrateRecord>[];
      for (final a in assignments) {
        if (a.retailerUserId <= 0) continue;
        CrateRecord? crate;
        try {
          crate = await CrateService.instance.getCratesForUser(a.retailerUserId);
        } catch (_) {}
        stores.add(CrateRecord(
          userId: a.retailerUserId,
          crateCount: crate?.crateCount ?? 0,
          crateReceived: crate?.crateReceived ?? 0,
          crateReturned: crate?.crateReturned ?? 0,
          recordedAt: crate?.recordedAt ?? DateTime.now(),
          holderName: a.shopName,
        ));
      }
      if (mounted) {
        setState(() {
          _own = own;
          _stores = stores;
        });
      }
    } catch (e) {
      if (mounted) setState(() => _error = apiErrorMessage(e));
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  int _atStore(CrateRecord s) {
    final n = s.crateReceived - s.crateReturned;
    return n < 0 ? 0 : n;
  }

  Future<int?> _askQty(String title) async {
    final controller = TextEditingController();
    return showDialog<int>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(title),
        content: TextField(
          controller: controller,
          keyboardType: TextInputType.number,
          decoration: const InputDecoration(labelText: 'Quantity'),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () =>
                Navigator.pop(context, int.tryParse(controller.text.trim()) ?? 0),
            child: const Text('Save'),
          ),
        ],
      ),
    );
  }

  Future<void> _sendToStore(CrateRecord store) async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    final qty = await _askQty('Send crates to ${store.holderName}');
    if (qty == null || qty <= 0) return;
    try {
      await CrateService.instance.sendToStore(
        salesmanId: session.userId,
        retailerUserId: store.userId,
        quantity: qty,
      );
      await _load();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(SnackBar(content: Text(apiErrorMessage(e))));
      }
    }
  }

  Future<void> _collectFromStore(CrateRecord store) async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    final qty = await _askQty('Collect crates from ${store.holderName}');
    if (qty == null || qty <= 0) return;
    try {
      await CrateService.instance.collectFromStore(
        salesmanId: session.userId,
        retailerUserId: store.userId,
        quantity: qty,
      );
      await _load();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(SnackBar(content: Text(apiErrorMessage(e))));
      }
    }
  }

  Future<void> _returnToBranch() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    final qty = await _askQty('Return crates to branch');
    if (qty == null || qty <= 0) return;
    try {
      await CrateService.instance.returnToBranch(
        salesmanId: session.userId,
        quantity: qty,
      );
      await _load();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(SnackBar(content: Text(apiErrorMessage(e))));
      }
    }
  }

  void _onNavTap(int index) {
    final Widget? destination = switch (index) {
      0 => const SalesmanDashboardScreen(),
      1 => const SalesmanActivityOrdersScreen(),
      2 => const SalesmanDeliveryPendingScreen(),
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
    final atStores = _stores.fold<int>(0, (sum, s) => sum + _atStore(s));
    return Scaffold(
      appBar: AppBar(
        title: const Text('Crates'),
        actions: [
          IconButton(
            tooltip: 'Return crates to branch',
            onPressed: _returnToBranch,
            icon: const Icon(Icons.assignment_return_outlined),
          ),
        ],
      ),
      bottomNavigationBar: AppBottomNav(currentIndex: 3, onTap: _onNavTap),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? LoadError(message: _error!, onRetry: _load)
              : RefreshIndicator(
                  onRefresh: _load,
                  child: ListView(
                    padding: const EdgeInsets.all(16),
                    children: [
                      StatCardGrid(cards: [
                        StatCard(
                          icon: Icons.inventory_2_outlined,
                          label: 'With me',
                          value: '${_own?.crateCount ?? 0}',
                        ),
                        StatCard(
                          icon: Icons.storefront_outlined,
                          label: 'At stores',
                          value: '$atStores',
                        ),
                        StatCard(
                          icon: Icons.local_shipping_outlined,
                          label: 'Sent to stores',
                          value: '${_own?.crateReceived ?? 0}',
                        ),
                        StatCard(
                          icon: Icons.assignment_return_outlined,
                          label: 'Back to branch',
                          value: '${_own?.crateReturned ?? 0}',
                        ),
                      ]),
                      const SizedBox(height: 16),
                      Card(
                        child: Padding(
                          padding: const EdgeInsets.all(16),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.stretch,
                            children: [
                              const Text('Return to branch',
                                  style: TextStyle(
                                      fontSize: 16,
                                      fontWeight: FontWeight.bold)),
                              const SizedBox(height: 4),
                              Text(
                                'Crates with you: ${_own?.crateCount ?? 0}. '
                                'Send unused crates back to the branch.',
                                style: const TextStyle(
                                    color: AppColors.textSecondary),
                              ),
                              const SizedBox(height: 12),
                              FilledButton.icon(
                                onPressed: _returnToBranch,
                                icon: const Icon(Icons.assignment_return_outlined),
                                label: const Text('Return crates to branch'),
                              ),
                            ],
                          ),
                        ),
                      ),
                      const SizedBox(height: 24),
                      const Text('Stores',
                          style: TextStyle(
                              fontSize: 16, fontWeight: FontWeight.bold)),
                      const SizedBox(height: 8),
                      if (_stores.isEmpty)
                        const Padding(
                          padding: EdgeInsets.symmetric(vertical: 24),
                          child: Center(
                            child: Text('No stores assigned.',
                                style: TextStyle(color: AppColors.textMuted)),
                          ),
                        )
                      else
                        ..._stores.map((c) {
                          final atStore = _atStore(c);
                          return Card(
                            margin: const EdgeInsets.only(bottom: 8),
                            child: ListTile(
                              leading: const CircleAvatar(
                                backgroundColor: AppColors.primaryLight,
                                child: Icon(Icons.storefront,
                                    color: AppColors.primary),
                              ),
                              title: Text(c.holderName ?? 'Store #${c.userId}'),
                              subtitle: Text(
                                  'Sent: ${c.crateCount}   At store: $atStore   Collected: ${c.crateReturned}'),
                              trailing: Wrap(
                                spacing: 4,
                                children: [
                                  TextButton(
                                    onPressed: () => _sendToStore(c),
                                    child: const Text('Send'),
                                  ),
                                  TextButton(
                                    onPressed: () => _collectFromStore(c),
                                    child: const Text('Collect'),
                                  ),
                                ],
                              ),
                            ),
                          );
                        }),
                    ],
                  ),
                ),
    );
  }
}
