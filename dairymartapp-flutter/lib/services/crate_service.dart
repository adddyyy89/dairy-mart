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
    if (list.isNotEmpty) return CrateRecord.fromJson(asMap(list.first));
    if (json is Map) return CrateRecord.fromJson(asMap(json));
    return null;
  }

  /// GET /crate/assigned/user/{id} returns a bare integer, not a crate list.
  Future<int> getAssignedCount(int userId) async {
    final json =
        await ApiClient.instance.get(ApiConfig.crateAssignedToUser(userId));
    return asInt(json);
  }

  Future<void> updateCrateRecord(CrateRecord record) async {
    await ApiClient.instance
        .post(ApiConfig.crateUpdate, body: record.toUpdateJson());
  }
}
