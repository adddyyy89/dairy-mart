import '../models/ledger.dart';
import 'api_client.dart';
import 'api_config.dart';

class LedgerService {
  LedgerService._();
  static final LedgerService instance = LedgerService._();

  Future<List<Ledger>> getLedgersForSalesman(int salesmanId) async {
    final json =
        await ApiClient.instance.get(ApiConfig.ledgerForSalesman(salesmanId));
    if (json is List) {
      return json.map((e) => Ledger.fromJson(e as Map<String, dynamic>)).toList();
    }
    return [];
  }

  /// The Postman collection doesn't expose a dedicated "transactions by
  /// ledger" GET, so this is wired against a conventional path -
  /// confirm against your controller (LedgerController / SalesmanLedgerController)
  /// and adjust the path if it differs.
  Future<List<LedgerTransaction>> getTransactions(int ledgerId) async {
    final json = await ApiClient.instance.get('/ledger/transactions/get/$ledgerId');
    if (json is List) {
      return json
          .map((e) => LedgerTransaction.fromJson(e as Map<String, dynamic>))
          .toList();
    }
    return [];
  }

  Future<void> addTransaction({
    required int ledgerId,
    required double amount,
    required bool isCredit,
    required int paymentTypeId,
    required int createdBy,
  }) async {
    await ApiClient.instance.post(
      ApiConfig.ledgerAdd,
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
