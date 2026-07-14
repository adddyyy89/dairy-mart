import 'dart:convert';
import 'package:flutter/foundation.dart' show debugPrint;
import 'package:http/http.dart' as http;
import 'api_config.dart';
import 'session_manager.dart';

class ApiException implements Exception {
  final int? statusCode;
  final String message;
  ApiException(this.message, {this.statusCode});

  @override
  String toString() => message;
}

/// Every screen's service class goes through here so auth headers, base URL,
/// timeouts, and error handling stay in exactly one place.
///
/// Every request/response is logged via debugPrint - visible in the VS Code
/// "Debug Console" (or the `flutter run` terminal) while the app is running
/// in debug mode. Logs are stripped automatically in release builds since
/// debugPrint is a no-op there.
class ApiClient {
  ApiClient._();
  static final ApiClient instance = ApiClient._();

  Uri _uri(String path, [Map<String, String>? query]) =>
      Uri.parse('${ApiConfig.baseUrl}$path')
          .replace(queryParameters: query);

  Map<String, String> _headers() {
    final headers = {'Content-Type': 'application/json'};
    final auth = SessionManager.instance.current?.basicAuthHeader;
    if (auth != null) headers['Authorization'] = auth;
    return headers;
  }

  Future<dynamic> get(String path, {Map<String, String>? query}) async {
    final uri = _uri(path, query);
    final headers = _headers();
    _logRequest('GET', uri, headers, null);
    try {
      final res = await http.get(uri, headers: headers).timeout(const Duration(seconds: 20));
      _logResponse('GET', uri, res);
      return _decode(res);
    } catch (e) {
      _logError('GET', uri, e);
      rethrow;
    }
  }

  Future<dynamic> post(String path, {Object? body}) async {
    final uri = _uri(path);
    final headers = _headers();
    _logRequest('POST', uri, headers, body);
    try {
      final res = await http
          .post(uri, headers: headers, body: jsonEncode(body))
          .timeout(const Duration(seconds: 20));
      _logResponse('POST', uri, res);
      return _decode(res);
    } catch (e) {
      _logError('POST', uri, e);
      rethrow;
    }
  }

  /// Login uses Basic Auth built from the raw credentials being submitted,
  /// mirroring MainActivity's request interceptor - it can't reuse the
  /// stored session because there isn't one yet.
  Future<dynamic> postWithBasicAuth(
    String path, {
    required String phoneNumber,
    required String password,
    Object? body,
  }) async {
    final uri = _uri(path);
    final credentials = base64Encode(utf8.encode('$phoneNumber:$password'));
    final headers = {
      'Content-Type': 'application/json',
      'Authorization': 'Basic $credentials',
    };
    _logRequest('POST', uri, headers, body);
    try {
      final res = await http
          .post(uri, headers: headers, body: jsonEncode(body))
          .timeout(const Duration(seconds: 20));
      _logResponse('POST', uri, res);
      return _decode(res);
    } catch (e) {
      _logError('POST', uri, e);
      rethrow;
    }
  }

  dynamic _decode(http.Response res) {
    if (res.statusCode >= 200 && res.statusCode < 300) {
      if (res.body.isEmpty) return null;
      return jsonDecode(res.body);
    }
    String message = 'Request failed (${res.statusCode})';
    try {
      final parsed = jsonDecode(res.body);
      if (parsed is Map && parsed['message'] != null) {
        message = parsed['message'];
      }
    } catch (_) {
      // response wasn't JSON - keep the generic message
    }
    throw ApiException(message, statusCode: res.statusCode);
  }

  // ---- Logging helpers -----------------------------------------------

  void _logRequest(String method, Uri uri, Map<String, String> headers, Object? body) {
    debugPrint('┌── API REQUEST ────────────────────────────');
    debugPrint('│ $method $uri');
    debugPrint('│ Auth: ${headers['Authorization'] ?? '(none)'}');
    if (body != null) {
      _logMultiline('│ Body: ', _tryEncode(body));
    }
    debugPrint('└───────────────────────────────────────────');
  }

  void _logResponse(String method, Uri uri, http.Response res) {
    debugPrint('┌── API RESPONSE ───────────────────────────');
    debugPrint('│ $method $uri -> ${res.statusCode}');
    _logMultiline('│ Body: ', res.body);
    debugPrint('└───────────────────────────────────────────');
  }

  void _logError(String method, Uri uri, Object error) {
    debugPrint('┌── API ERROR ──────────────────────────────');
    debugPrint('│ $method $uri');
    debugPrint('│ $error');
    debugPrint('└───────────────────────────────────────────');
  }

  String _tryEncode(Object? body) {
    try {
      return jsonEncode(body);
    } catch (_) {
      return body.toString();
    }
  }

  /// debugPrint / adb logcat truncate very long single lines - this splits
  /// long request/response bodies into ~800-char chunks so nothing gets cut
  /// off when reading logs in the VS Code Debug Console or `flutter run`
  /// terminal.
  void _logMultiline(String prefix, String content) {
    const chunkSize = 800;
    if (content.length <= chunkSize) {
      debugPrint('$prefix$content');
      return;
    }
    for (var i = 0; i < content.length; i += chunkSize) {
      final end = (i + chunkSize < content.length) ? i + chunkSize : content.length;
      debugPrint('$prefix${content.substring(i, end)}');
    }
  }
}