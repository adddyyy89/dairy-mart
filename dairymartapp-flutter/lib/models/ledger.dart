/// Maps to LedgerDTO - the running account between one salesman and one retailer.
class Ledger {
  final int ledgerId;
  final int salesmanId;
  final int retailerId;
  final String? retailerName;
  final String? salesmanName;
  final bool active;

  Ledger({
    required this.ledgerId,
    required this.salesmanId,
    required this.retailerId,
    this.retailerName,
    this.salesmanName,
    this.active = true,
  });

  factory Ledger.fromJson(Map<String, dynamic> json) {
    final retailer = json['retailer'] as Map<String, dynamic>?;
    final salesman = json['salesman'] as Map<String, dynamic>?;
    return Ledger(
      ledgerId: json['ledgerId'] ?? 0,
      salesmanId: json['salesmanId'] ?? 0,
      retailerId: json['retailerId'] ?? 0,
      retailerName: retailer?['firstName'],
      salesmanName: salesman?['firstName'],
      active: json['active'] ?? true,
    );
  }
}

/// Maps to LedgerTransactionsDTO - individual credit/debit entries.
class LedgerTransaction {
  final int transactionId;
  final int ledgerId;
  final double amount;
  final bool isCredit;
  final bool isDebit;
  final String? paymentTypeDesc;
  final DateTime createdOn;

  LedgerTransaction({
    required this.transactionId,
    required this.ledgerId,
    required this.amount,
    required this.isCredit,
    required this.isDebit,
    this.paymentTypeDesc,
    required this.createdOn,
  });

  /// Positive amount == money coming in (credit), negative == outstanding/debit,
  /// matching the red/black amount coloring seen across the legacy layouts.
  double get signedAmount => isDebit ? -amount : amount;

  factory LedgerTransaction.fromJson(Map<String, dynamic> json) {
    final paymentType = json['paymentType'] as Map<String, dynamic>?;
    return LedgerTransaction(
      transactionId: json['transactionsId'] ?? 0,
      ledgerId: json['ledgerId'] ?? 0,
      amount: (json['amount'] is num)
          ? (json['amount'] as num).toDouble()
          : double.tryParse(json['amount']?.toString() ?? '0') ?? 0,
      isCredit: json['credit'] ?? false,
      isDebit: json['debit'] ?? false,
      paymentTypeDesc: paymentType?['paymentTypeDesc'],
      createdOn: DateTime.tryParse(json['createdOn']?.toString() ?? '') ??
          DateTime.now(),
    );
  }
}

/// Aggregated summary shown at the top of the ledger dashboard
/// (balance / outstanding / wallet cards in activity_salesman_ledger_dashboard.xml)
class LedgerSummary {
  final double balance;
  final double outstanding;
  final double wallet;

  const LedgerSummary({
    required this.balance,
    required this.outstanding,
    required this.wallet,
  });

  factory LedgerSummary.fromJson(Map<String, dynamic> json) => LedgerSummary(
        balance: (json['balance'] as num?)?.toDouble() ?? 0,
        outstanding: (json['outstanding'] as num?)?.toDouble() ?? 0,
        wallet: (json['wallet'] as num?)?.toDouble() ?? 0,
      );
}
