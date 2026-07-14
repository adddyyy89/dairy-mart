/// The backend serializes this endpoint's DTOs as raw maps - every object,
/// including nested ones (ledger, retailer, salesman, type, paymentType),
/// arrives wrapped as {"map": {...actual fields...}} instead of a clean
/// POJO shape. `_unwrap` strips that one consistent layer wherever it shows
/// up. This is specific to this endpoint - other endpoints (e.g. /auth/login)
/// return normal unwrapped JSON.
Map<String, dynamic> _unwrap(dynamic node) {
  if (node is Map) {
    final map = Map<String, dynamic>.from(node);
    if (map.containsKey('map') && map['map'] is Map) {
      return Map<String, dynamic>.from(map['map'] as Map);
    }
    return map;
  }
  return {};
}

/// One entry from `recenttransactions.myArrayList`.
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
    final ledger = _unwrap(json['ledger']);
    final retailer = _unwrap(ledger['retailer']);

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
    final json = _unwrap(raw.containsKey('map') ? raw : {'map': raw});

    double parseAmount(dynamic v) {
      if (v == null) return 0;
      if (v is num) return v.toDouble();
      return double.tryParse(v.toString()) ?? 0;
    }

    final txContainer = json['recenttransactions'];
    final txList = (txContainer is Map && txContainer['myArrayList'] is List)
        ? txContainer['myArrayList'] as List
        : const [];

    return SalesmanDashboardSummary(
      salesmanName: json['salesmanname']?.toString() ?? '',
      salesmanPhoneNumber: json['salesmanphonenumber']?.toString() ?? '',
      cratesAssigned: json['cratesassigned'] ?? 0,
      walletBalance: parseAmount(json['walletbalance']),
      ordersPlaced: json['ordersplaced'] ?? 0,
      recentTransactions: txList
          .map((e) => RecentTransaction.fromJson(_unwrap(e)))
          .toList(growable: false),
    );
  }
}
