import 'dart:async';
import 'dart:convert';
import 'package:flutter/foundation.dart' show debugPrint;
import 'package:http/http.dart' as http;
import '../utils/json_unwrap.dart';
import 'api_config.dart';
import 'session_manager.dart';

const String kServerDownMessage = 'The server is down. Please try again later.';

class ApiException implements Exception {
  final int? statusCode;
  final String message;
  ApiException(this.message, {this.statusCode});

  @override
  String toString() => message;
}

/// Every screen's service class goes through here so auth headers, base URL,
/// timeouts, and error handling stay in exactly one place.
class ApiClient {
  ApiClient._();
  static final ApiClient instance = ApiClient._();

  Uri _uri(String path, [Map<String, String>? query]) =>
      Uri.parse('${ApiConfig.baseUrl}$path')
          .replace(queryParameters: query);

  Map<String, String> _headers() {
    final headers = {
      'Content-Type': 'application/json',
      'Cache-Control': 'no-cache, no-store',
      'Pragma': 'no-cache',
    };
    final auth = SessionManager.instance.current?.basicAuthHeader;
    if (auth != null) headers['Authorization'] = auth;
    return headers;
  }

  Future<dynamic> get(String path, {Map<String, String>? query}) async {
    final uri = _uri(path, query);
    final headers = _headers();
    _logRequest('GET', uri, null);
    try {
      final res = await http.get(uri, headers: headers).timeout(const Duration(seconds: 20));
      _logResponse('GET', uri, res);
      return _decode(res);
    } catch (e) {
      _logError('GET', uri, e);
      throw _asApiException(e);
    }
  }

  Future<dynamic> post(String path, {Object? body}) async {
    final uri = _uri(path);
    final headers = _headers();
    _logRequest('POST', uri, body);
    try {
      final res = await http
          .post(uri, headers: headers, body: jsonEncode(body))
          .timeout(const Duration(seconds: 20));
      _logResponse('POST', uri, res);
      return _decode(res);
    } catch (e) {
      _logError('POST', uri, e);
      throw _asApiException(e);
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
    _logRequest('POST', uri, body);
    try {
      final res = await http
          .post(uri, headers: headers, body: jsonEncode(body))
          .timeout(const Duration(seconds: 20));
      _logResponse('POST', uri, res);
      return _decode(res);
    } catch (e) {
      _logError('POST', uri, e);
      throw _asApiException(e);
    }
  }

  dynamic _decode(http.Response res) {
    final trimmed = res.body.trim();
    dynamic parsed;
    try {
      if (trimmed.isNotEmpty) {
        parsed = unwrapJson(jsonDecode(trimmed));
      }
    } catch (_) {
      parsed = trimmed.isEmpty ? null : trimmed;
    }

    if (res.statusCode >= 200 && res.statusCode < 300) {
      if (parsed == null && trimmed.isEmpty) return null;
      if (parsed == null) return int.tryParse(trimmed) ?? trimmed;
      return parsed;
    }

    throw ApiException(
      _errorText(res.statusCode, parsed, trimmed),
      statusCode: res.statusCode,
    );
  }

  String _errorText(int status, dynamic parsed, String raw) {
    final fromBody = _messageFromBody(parsed, raw);
    if (fromBody != null && fromBody.isNotEmpty) {
      return fromBody;
    }
    if (status == 400) {
      return 'That request was not valid. Check the details and try again.';
    }
    if (status == 401) {
      return 'Your session expired. Please sign in again.';
    }
    if (status == 403) {
      return 'You are not allowed to do this.';
    }
    if (status == 404) {
      return 'That record was not found.';
    }
    if (status >= 500) {
      return kServerDownMessage;
    }
    return 'Request failed ($status)';
  }

  String? _messageFromBody(dynamic parsed, String raw) {
    if (parsed is String) {
      final text = parsed.trim();
      if (text.isEmpty || text.startsWith('<')) return null;
      return text;
    }
    if (parsed is Map) {
      for (final key in ['message', 'error', 'errorMessage', 'detail']) {
        final value = parsed[key];
        if (value is String && value.trim().isNotEmpty) {
          return value.trim();
        }
      }
    }
    if (raw.isNotEmpty &&
        !raw.startsWith('<') &&
        !raw.startsWith('{') &&
        !raw.startsWith('[')) {
      return raw;
    }
    return null;
  }

  ApiException _asApiException(Object error) {
    if (error is ApiException) return error;
    return ApiException(kServerDownMessage);
  }

  void _logRequest(String method, Uri uri, Object? body) {
    debugPrint('[API] $method $uri');
    if (body != null) {
      debugPrint('[API] body ${_tryEncode(body)}');
    }
  }

  void _logResponse(String method, Uri uri, http.Response res) {
    debugPrint('[API] $method $uri -> ${res.statusCode} (${res.body.length} bytes)');
  }

  void _logError(String method, Uri uri, Object error) {
    debugPrint('[API] ERROR $method $uri :: $error');
  }

  String _tryEncode(Object? body) {
    try {
      return jsonEncode(body);
    } catch (_) {
      return body.toString();
    }
  }
}
