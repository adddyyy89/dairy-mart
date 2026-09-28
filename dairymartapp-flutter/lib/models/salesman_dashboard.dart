import '../utils/json_unwrap.dart';

/// One entry from `recenttransactions`.
class RecentTransaction {
  final int transactionId;
  final double amount;
  final bool isCredit;
  final DateTime date;
  final String retailerName;

  RecentTransaction({
    required this.transactionId,
    required this.amount,
    required this.isCredit,
    required this.date,
    required this.retailerName,
  });

  /// Positive for credit, negative for debit - drives both the sign and
  /// the color shown in the UI.
  double get signedAmount => isCredit ? amount : -amount;

  factory RecentTransaction.fromJson(Map<String, dynamic> json) {
    final ledger = asMap(json['ledger']);
    final retailer = asMap(ledger['retailer']);

    // The retailer object here is the User record (firstName/lastName), not
    // the Shop record - there's no shopName in this payload. If you need the
    // actual shop name, fetch it separately via GET /shop/get/user/{retailerId}
    // and merge it in.
    final firstName = retailer['firstName']?.toString() ?? '';
    final lastName = retailer['lastName']?.toString() ?? '';
    final name = '$firstName $lastName'.trim();

    double parseAmount(dynamic v) {
      if (v == null) return 0;
      if (v is num) return v.toDouble();
      return double.tryParse(v.toString()) ?? 0;
    }

    return RecentTransaction(
      transactionId: json['transactionsId'] ?? 0,
      amount: parseAmount(json['amount']),
      isCredit: json['credit'] == true,
      date: DateTime.tryParse(json['createdOn']?.toString() ?? '') ?? DateTime.now(),
      retailerName: name.isEmpty ? 'Retailer' : name,
    );
  }
}

/// Full response shape of GET /salesman/dashboard/get/{userId}.
class SalesmanDashboardSummary {
  final String salesmanName;
  final String salesmanPhoneNumber;
  final int cratesAssigned;
  final double walletBalance;
  final int ordersPlaced;
  final List<RecentTransaction> recentTransactions;

  SalesmanDashboardSummary({
    required this.salesmanName,
    required this.salesmanPhoneNumber,
    required this.cratesAssigned,
    required this.walletBalance,
    required this.ordersPlaced,
    required this.recentTransactions,
  });

  factory SalesmanDashboardSummary.fromJson(Map<String, dynamic> raw) {
    final json = asMap(raw);

    double parseAmount(dynamic v) {
      if (v == null) return 0;
      if (v is num) return v.toDouble();
      return double.tryParse(v.toString()) ?? 0;
    }

    return SalesmanDashboardSummary(
      salesmanName: json['salesmanname']?.toString() ?? '',
      salesmanPhoneNumber: json['salesmanphonenumber']?.toString() ?? '',
      cratesAssigned: asInt(json['cratesassigned']),
      walletBalance: parseAmount(json['walletbalance']),
      ordersPlaced: asInt(json['ordersplaced']),
      recentTransactions: asList(json['recenttransactions'])
          .map((e) => RecentTransaction.fromJson(asMap(e)))
          .toList(growable: false),
    );
  }
}
