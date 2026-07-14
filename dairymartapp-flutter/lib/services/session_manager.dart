import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/user.dart';

/// Equivalent of the legacy app's `getSharedPreferences("dairymart", ...)`
/// block used across MainActivity / *DashboardActivity. Persists userid,
/// usertypeid (role), phoneNumber, and the Basic Auth header built from
/// phone:password.
class SessionManager {
  SessionManager._();
  static final SessionManager instance = SessionManager._();

  static const _keyUserId = 'userid';
  static const _keyRole = 'usertypeid';
  static const _keyAuth = 'auth';
  static const _keyPhone = 'phonenumber';

  AuthSession? _cached;

  AuthSession? get current => _cached;

  /// Set once when a persisted session is restored on app start (not on a
  /// fresh login, since the login screen already gives its own feedback).
  /// A dashboard screen calls [consumeStartupNotice] on its first frame to
  /// show a one-time "you're still logged in as..." toast, then it's
  /// cleared so it never shows again for that app run.
  String? _pendingStartupNotice;

  String? consumeStartupNotice() {
    final notice = _pendingStartupNotice;
    _pendingStartupNotice = null;
    return notice;
  }

  String _roleLabel(UserRole role) {
    switch (role) {
      case UserRole.admin:
        return 'Admin';
      case UserRole.salesman:
        return 'Salesman';
      case UserRole.retailer:
        return 'Retailer';
      case UserRole.unknown:
        return 'Unknown';
    }
  }

  Future<AuthSession?> load() async {
    final prefs = await SharedPreferences.getInstance();
    final userId = prefs.getInt(_keyUserId);
    final roleId = prefs.getInt(_keyRole);
    final auth = prefs.getString(_keyAuth);
    final phoneNumber = prefs.getString(_keyPhone) ?? '';
    if (userId == null || roleId == null || auth == null) {
      _cached = null;
      return null;
    }
    _cached = AuthSession(
      userId: userId,
      role: userRoleFromId(roleId),
      phoneNumber: phoneNumber,
      basicAuthHeader: auth,
    );

    final phoneLabel = phoneNumber.isNotEmpty ? phoneNumber : 'unknown number';
    _pendingStartupNotice =
        'Logged in as ${_roleLabel(_cached!.role)} • $phoneLabel • User ID ${_cached!.userId}';

    return _cached;
  }

  Future<void> save({
    required int userId,
    required int roleId,
    required String phoneNumber,
    required String password,
  }) async {
    final prefs = await SharedPreferences.getInstance();
    final credentials = '$phoneNumber:$password';
    final auth = 'Basic ${base64Encode(utf8.encode(credentials))}';

    await prefs.setInt(_keyUserId, userId);
    await prefs.setInt(_keyRole, roleId);
    await prefs.setString(_keyAuth, auth);
    await prefs.setString(_keyPhone, phoneNumber);

    _cached = AuthSession(
      userId: userId,
      role: userRoleFromId(roleId),
      phoneNumber: phoneNumber,
      basicAuthHeader: auth,
    );
  }

  Future<void> clear() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_keyUserId);
    await prefs.remove(_keyRole);
    await prefs.remove(_keyAuth);
    await prefs.remove(_keyPhone);
    _cached = null;
  }
}