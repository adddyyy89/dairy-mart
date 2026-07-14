import '../models/user.dart';
import 'api_client.dart';
import 'api_config.dart';
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

    final isActive = response['isActive'] == true;
    if (!isActive) {
      throw ApiException('This account is deactivated. Contact your admin.');
    }

    final userId = response['userId'] as int;
    final roleId = response['role'] as int;

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
    try {
      await ApiClient.instance.post(ApiConfig.logout);
    } catch (_) {
      // best-effort - still clear local session even if the call fails
    }
    await SessionManager.instance.clear();
  }
}