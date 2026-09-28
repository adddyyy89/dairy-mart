import '../models/ledger.dart';
import '../utils/json_unwrap.dart';
import 'api_client.dart';
import 'api_config.dart';

class SalesmanLedgerDashboard {
  final double wallet;
  final double outstanding;
  final List<LedgerAmount> ledgers;

  const SalesmanLedgerDashboard({
    required this.wallet,
    required this.outstanding,
    required this.ledgers,
  });

  double get balance => wallet + outstanding;
}

class LedgerAmount {
  final int ledgerId;
  final double amount;
  final Ledger ledger;

  const LedgerAmount({
    required this.ledgerId,
    required this.amount,
    required this.ledger,
  });
}

class LedgerDetail {
  final String retailerName;
  final String retailerAddress;
  final double balance;
  final List<LedgerTransaction> transactions;

  const LedgerDetail({
    required this.retailerName,
    required this.retailerAddress,
    required this.balance,
    required this.transactions,
  });
}

class LedgerService {
  LedgerService._();
  static final LedgerService instance = LedgerService._();

  Future<SalesmanLedgerDashboard> getSalesmanDashboard(int salesmanId) async {
    final json = await ApiClient.instance
        .get(ApiConfig.salesmanLedgerDashboard(salesmanId));
    final map = asMap(json);
    final rows = asList(map['ledgersummary']);
    return SalesmanLedgerDashboard(
      wallet: asDouble(map['walletbalance']),
      outstanding: asDouble(map['outstanding']),
      ledgers: rows.map((e) {
        final row = asMap(e);
        return LedgerAmount(
          ledgerId: asInt(row['ledgerId']),
          amount: asDouble(row['amount']),
          ledger: Ledger.fromJson(asMap(row['ledger'])),
        );
      }).toList(),
    );
  }

  Future<LedgerDetail> getLedgerDetail(int ledgerId) async {
    final json = await ApiClient.instance.get(ApiConfig.ledgerById(ledgerId));
    final map = asMap(json);
    final txs = asList(map['transactionsDTOS']);
    return LedgerDetail(
      retailerName: map['retailerName']?.toString() ?? 'Retailer',
      retailerAddress: map['retailerAddress']?.toString() ?? '',
      balance: asDouble(map['balance']),
      transactions:
          txs.map((e) => LedgerTransaction.fromJson(asMap(e))).toList(),
    );
  }

  Future<void> addPayment({
    required int ledgerId,
    required double amount,
    required bool isCredit,
    required int createdBy,
    int paymentTypeId = 1,
  }) async {
    await ApiClient.instance.post(
      ApiConfig.ledgerSalesmanUpdate,
      body: {
        'ledgerId': ledgerId,
        'amount': amount,
        'credit': isCredit,
        'debit': !isCredit,
        'paymentTypeId': paymentTypeId,
        'createdBy': createdBy,
      },
    );
  }
}
