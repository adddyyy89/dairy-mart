import '../models/user.dart';
import 'api_client.dart';
import 'api_config.dart';

class UserService {
  UserService._();
  static final UserService instance = UserService._();

  Future<AppUser> getUser(int userId) async {
    final json = await ApiClient.instance.get(ApiConfig.userGet(userId));
    return AppUser.fromJson(json as Map<String, dynamic>);
  }

  Future<void> updateUser(Map<String, dynamic> payload) async {
    await ApiClient.instance.post(ApiConfig.userUpdate, body: payload);
  }
}
