import '../models/user.dart';
import '../utils/json_unwrap.dart';
import 'api_client.dart';
import 'api_config.dart';

class UserService {
  UserService._();
  static final UserService instance = UserService._();

  Future<AppUser> getUser(int userId) async {
    final json = await ApiClient.instance.get(ApiConfig.userGet(userId));
    return AppUser.fromJson(asMap(json));
  }

  Future<List<AppUser>> getAllUsers() async {
    final json = await ApiClient.instance.get(ApiConfig.adminUsers);
    final map = asMap(json);
    return asList(map['users'] ?? json)
        .map((e) => AppUser.fromJson(asMap(e)))
        .toList();
  }

  Future<void> updateUser(Map<String, dynamic> payload) async {
    await ApiClient.instance.post(ApiConfig.userUpdate, body: payload);
  }
}
