import '../models/user.dart';
import '../utils/json_unwrap.dart';
import 'api_client.dart';
import 'api_config.dart';
import 'location_ping.dart';
import 'session_manager.dart';

/// Response shape (docs/json responses/login.txt):
/// {"phoneNumber":"...","userId":9,"loggedIn":"...","role":3,"isActive":true}
class AuthService {
  AuthService._();
  static final AuthService instance = AuthService._();

  Future<AuthSession> login({
    required String phoneNumber,
    required String password,
  }) async {
    final response = await ApiClient.instance.postWithBasicAuth(
      ApiConfig.login,
      phoneNumber: phoneNumber,
      password: password,
      body: {'phoneNumber': phoneNumber, 'password': password},
    );

    final map = asMap(response);
    final isActive = map['isActive'] == true;
    if (!isActive) {
      throw ApiException('This account is deactivated. Contact your admin.');
    }

    final userId = asInt(map['userId']);
    final roleId = asInt(map['role']);

    await SessionManager.instance.save(
      userId: userId,
      roleId: roleId,
      phoneNumber: phoneNumber,
      password: password,
    );

    return AuthSession(
      userId: userId,
      role: userRoleFromId(roleId),
      phoneNumber: phoneNumber,
      basicAuthHeader: SessionManager.instance.current!.basicAuthHeader,
    );
  }

  /// POST /auth/logout, authenticated with the same Basic Auth header used
  /// throughout the session (built from phone:password at login time via
  /// ApiClient._headers() -> SessionManager.instance.current.basicAuthHeader).
  /// Must fire before clearing the local session, or there'd be no
  /// Authorization header left to send.
  Future<void> logout() async {
    final session = SessionManager.instance.current;
    try {
      await ApiClient.instance.post(
        ApiConfig.logout,
        body: {
          'phoneNumber': session?.phoneNumber ?? '',
          'userId': session?.userId ?? 0,
        },
      );
    } catch (_) {
      // best-effort - still clear local session even if the call fails
    }
    await LocationPing.instance.stop();
    await SessionManager.instance.clear();
  }

  Future<void> resetPassword({
    required String phoneNumber,
    required String newPassword,
  }) async {
    await ApiClient.instance.post(
      ApiConfig.resetPassword,
      body: {'phoneNumber': phoneNumber, 'newPassword': newPassword},
    );
  }
}