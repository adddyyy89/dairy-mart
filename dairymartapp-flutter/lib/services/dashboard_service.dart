import '../models/ledger.dart';
import '../models/salesman_dashboard.dart';
import '../utils/json_unwrap.dart';
import 'api_client.dart';
import 'api_config.dart';
import 'crate_service.dart';

class DashboardStats {
  final double balance;
  final int ordersPlaced;
  final int cratesAssigned;
  final int cratesEngaged;
  final List<LedgerTransaction> recentTransactions;

  const DashboardStats({
    required this.balance,
    required this.ordersPlaced,
    required this.cratesAssigned,
    required this.cratesEngaged,
    this.recentTransactions = const [],
  });
}

class DashboardService {
  DashboardService._();
  static final DashboardService instance = DashboardService._();

  Future<SalesmanDashboardSummary> getSalesmanDashboard(int salesmanId) async {
    final json =
        await ApiClient.instance.get(ApiConfig.salesmanDashboard(salesmanId));
    return SalesmanDashboardSummary.fromJson(asMap(json));
  }

  Future<DashboardStats> getRetailerDashboard(int retailerId) async {
    final json =
        await ApiClient.instance.get(ApiConfig.retailerDashboard(retailerId));
    final map = asMap(json);
    int engaged = 0;
    try {
      final crate = await CrateService.instance.getCratesForUser(retailerId);
      engaged = crate?.engaged ?? 0;
    } catch (_) {}
    return DashboardStats(
      balance: asDouble(map['walletbalance'] ?? map['balance']),
      ordersPlaced: asInt(map['ordersplaced'] ?? map['ordersPlaced']),
      cratesAssigned: asInt(map['cratesassigned'] ?? map['cratesAssigned']),
      cratesEngaged: engaged,
      recentTransactions: asList(map['recenttransactions'])
          .map((e) => LedgerTransaction.fromJson(asMap(e)))
          .toList(),
    );
  }
}
