import 'package:flutter/material.dart';
import '../../models/crate.dart';
import '../../services/crate_service.dart';
import '../../services/session_manager.dart';
import '../../theme/app_theme.dart';
import '../../widgets/app_bottom_nav.dart';
import '../../widgets/stat_card.dart';
import 'salesman_activity_orders_screen.dart';
import 'salesman_dashboard_screen.dart';
import 'salesman_delivery_pending_screen.dart';
import 'salesman_ledger_dashboard_screen.dart';

/// Crate management screen - built from CrateDTO (userId, crateCount,
/// crateReceived, crateReturned). Shows the salesman's own crate totals plus
/// the per-retailer breakdown of crates handed out vs returned, with a quick
/// "log return" action.
class SalesmanCratesScreen extends StatefulWidget {
  const SalesmanCratesScreen({super.key});

  @override
  State<SalesmanCratesScreen> createState() => _SalesmanCratesScreenState();
}

class _SalesmanCratesScreenState extends State<SalesmanCratesScreen> {
  bool _isLoading = true;
  CrateRecord? _own;
  List<CrateRecord> _assigned = [];

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
      final own = await CrateService.instance.getCratesForUser(session.userId);
      final assigned = await CrateService.instance.getAssignedToUser(session.userId);
      if (mounted) {
        setState(() {
          _own = own;
          _assigned = assigned;
        });
      }
    } catch (_) {
      // real app: show retry state
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  Future<void> _logReturn(CrateRecord record) async {
    final controller = TextEditingController();
    final qty = await showDialog<int>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Log crate return - ${record.holderName ?? 'Retailer #${record.userId}'}'),
        content: TextField(
          controller: controller,
          keyboardType: TextInputType.number,
          decoration: const InputDecoration(labelText: 'Crates returned'),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(context, int.tryParse(controller.text) ?? 0),
            child: const Text('Save'),
          ),
        ],
      ),
    );

    if (qty == null || qty <= 0) return;

    final updated = CrateRecord(
      userId: record.userId,
      crateCount: record.crateCount,
      crateReceived: record.crateReceived,
      crateReturned: record.crateReturned + qty,
      recordedAt: DateTime.now(),
      holderName: record.holderName,
    );

    try {
      await CrateService.instance.updateCrateRecord(updated);
      _load();
    } catch (_) {
      if (mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(const SnackBar(content: Text('Could not update crate record.')));
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
      Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => destination));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Crates')),
      bottomNavigationBar: AppBottomNav(currentIndex: 3, onTap: _onNavTap),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _load,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  StatCardGrid(cards: [
                    StatCard(
                      icon: Icons.inventory_2_outlined,
                      label: 'Assigned to me',
                      value: '${_own?.crateCount ?? 0}',
                    ),
                    StatCard(
                      icon: Icons.local_shipping_outlined,
                      label: 'Handed out',
                      value: '${_own?.crateReceived ?? 0}',
                    ),
                    StatCard(
                      icon: Icons.assignment_return_outlined,
                      label: 'Returned',
                      value: '${_own?.crateReturned ?? 0}',
                    ),
                    StatCard(
                      icon: Icons.warning_amber_outlined,
                      label: 'Engaged',
                      value: '${_own?.engaged ?? 0}',
                      valueColor: (_own?.engaged ?? 0) > 0 ? AppColors.warning : null,
                    ),
                  ]),
                  const SizedBox(height: 24),
                  const Text('By Retailer',
                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 8),
                  if (_assigned.isEmpty)
                    const Padding(
                      padding: EdgeInsets.symmetric(vertical: 24),
                      child: Center(
                        child: Text('No crates currently assigned to retailers.',
                            style: TextStyle(color: AppColors.textMuted)),
                      ),
                    )
                  else
                    ..._assigned.map((c) => Card(
                          margin: const EdgeInsets.only(bottom: 8),
                          child: ListTile(
                            leading: const CircleAvatar(
                              backgroundColor: AppColors.primaryLight,
                              child: Icon(Icons.storefront, color: AppColors.primary),
                            ),
                            title: Text(c.holderName ?? 'Retailer #${c.userId}'),
                            subtitle: Text(
                                'Out: ${c.crateReceived}   Returned: ${c.crateReturned}   Engaged: ${c.engaged}'),
                            trailing: TextButton(
                              onPressed: () => _logReturn(c),
                              child: const Text('Log Return'),
                            ),
                          ),
                        )),
                ],
              ),
            ),
    );
  }
}
