import '../models/crate.dart';
import '../utils/json_unwrap.dart';
import 'api_client.dart';
import 'api_config.dart';

class CrateService {
  CrateService._();
  static final CrateService instance = CrateService._();

  Future<List<CrateRecord>> getAllCrates() async {
    final json = await ApiClient.instance.get(ApiConfig.crateGetAll);
    return asList(json).map((e) => CrateRecord.fromJson(asMap(e))).toList();
  }

  Future<CrateRecord?> getCratesForUser(int userId) async {
    final json = await ApiClient.instance.get(ApiConfig.crateGetByUser(userId));
    final list = asList(json);
    if (list.isEmpty) return null;
    return CrateRecord.fromJson(asMap(list.first));
  }

  Future<void> sendToStore({
    required int salesmanId,
    required int retailerUserId,
    required int quantity,
  }) async {
    await ApiClient.instance.post(ApiConfig.crateStoreSend, body: {
      'salesmanId': salesmanId,
      'retailerUserId': retailerUserId,
      'quantity': quantity,
    });
  }

  Future<void> collectFromStore({
    required int salesmanId,
    required int retailerUserId,
    required int quantity,
  }) async {
    await ApiClient.instance.post(ApiConfig.crateStoreReturn, body: {
      'salesmanId': salesmanId,
      'retailerUserId': retailerUserId,
      'quantity': quantity,
    });
  }

  Future<void> returnToBranch({
    required int salesmanId,
    required int quantity,
  }) async {
    await ApiClient.instance.post(ApiConfig.crateBranchReturn, body: {
      'salesmanId': salesmanId,
      'quantity': quantity,
    });
  }

  /// GET /crate/assigned/user/{id} returns a bare integer, not a crate list.
  Future<int> getAssignedCount(int userId) async {
    final json =
        await ApiClient.instance.get(ApiConfig.crateAssignedToUser(userId));
    return asInt(json);
  }
}
