import '../models/crate.dart';
import 'api_client.dart';
import 'api_config.dart';

class CrateService {
  CrateService._();
  static final CrateService instance = CrateService._();

  Future<List<CrateRecord>> getAllCrates() async {
    final json = await ApiClient.instance.get(ApiConfig.crateGetAll);
    return (json as List<dynamic>)
        .map((e) => CrateRecord.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<CrateRecord?> getCratesForUser(int userId) async {
    final json = await ApiClient.instance.get(ApiConfig.crateGetByUser(userId));
    if (json == null) return null;
    if (json is List && json.isNotEmpty) {
      return CrateRecord.fromJson(json.first as Map<String, dynamic>);
    }
    if (json is Map<String, dynamic>) return CrateRecord.fromJson(json);
    return null;
  }

  Future<List<CrateRecord>> getAssignedToUser(int userId) async {
    final json =
        await ApiClient.instance.get(ApiConfig.crateAssignedToUser(userId));
    return (json as List<dynamic>)
        .map((e) => CrateRecord.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<void> updateCrateRecord(CrateRecord record) async {
    await ApiClient.instance
        .post(ApiConfig.crateUpdate, body: record.toUpdateJson());
  }
}
