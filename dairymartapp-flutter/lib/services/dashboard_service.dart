import '../models/ledger.dart';
import '../models/salesman_dashboard.dart';
import 'api_client.dart';
import 'api_config.dart';

/// Wraps /retailer/dashboard/get/{id}. The exact response shape wasn't in
/// the sample JSON dump (unlike the salesman endpoint - see
/// SalesmanDashboardSummary for that confirmed shape), so this parses
/// defensively and falls back to zeros - swap the field names below for the
/// real ones once you confirm them against a live response.
class DashboardStats {
  final double balance;
  final int ordersPlaced;
  final int cratesAssigned;
  final int cratesEngaged;

  const DashboardStats({
    required this.balance,
    required this.ordersPlaced,
    required this.cratesAssigned,
    required this.cratesEngaged,
  });

  factory DashboardStats.fromJson(Map<String, dynamic> json) => DashboardStats(
        balance: (json['balance'] as num?)?.toDouble() ?? 0,
        ordersPlaced: json['ordersPlaced'] ?? json['orderPlaced'] ?? 0,
        cratesAssigned: json['cratesAssigned'] ?? json['crateAssigned'] ?? 0,
        cratesEngaged: json['cratesEngaged'] ?? json['engagedCrate'] ?? 0,
      );
}

class DashboardService {
  DashboardService._();
  static final DashboardService instance = DashboardService._();

  Future<SalesmanDashboardSummary> getSalesmanDashboard(int salesmanId) async {
    final json =
        await ApiClient.instance.get(ApiConfig.salesmanDashboard(salesmanId));
    return SalesmanDashboardSummary.fromJson(json as Map<String, dynamic>);
  }

  Future<DashboardStats> getRetailerDashboard(int retailerId) async {
    final json =
        await ApiClient.instance.get(ApiConfig.retailerDashboard(retailerId));
    return DashboardStats.fromJson(json as Map<String, dynamic>);
  }

  Future<LedgerSummary> getLedgerSummary(int userId) async {
    // Ledger summary is derived client-side from the transaction list until
    // a dedicated summary endpoint is confirmed - see LedgerService.
    final json =
        await ApiClient.instance.get(ApiConfig.ledgerForSalesman(userId));
    if (json is Map<String, dynamic>) return LedgerSummary.fromJson(json);
    return const LedgerSummary(balance: 0, outstanding: 0, wallet: 0);
  }
}
