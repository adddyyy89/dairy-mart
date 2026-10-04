/// Gson often serializes org.json.JSONObject / JSONArray as
/// `{map: {...}}` and `{myArrayList: [...]}`. Strip that layer
/// recursively so the rest of the app can read normal JSON.
dynamic unwrapJson(dynamic node) {
  if (node is Map) {
    final map = Map<String, dynamic>.from(node);
    if (map.length == 1 && map.containsKey('map')) {
      return unwrapJson(map['map']);
    }
    if (map.length == 1 && map.containsKey('myArrayList')) {
      return unwrapJson(map['myArrayList']);
    }
    return {for (final e in map.entries) e.key.toString(): unwrapJson(e.value)};
  }
  if (node is List) {
    return node.map(unwrapJson).toList();
  }
  return node;
}

Map<String, dynamic> asMap(dynamic node) {
  final unwrapped = unwrapJson(node);
  if (unwrapped is Map) {
    return Map<String, dynamic>.from(unwrapped);
  }
  return {};
}

List<dynamic> asList(dynamic node) {
  final unwrapped = unwrapJson(node);
  if (unwrapped is List) return unwrapped;
  if (unwrapped is Map && unwrapped['myArrayList'] is List) {
    return List<dynamic>.from(unwrapped['myArrayList'] as List);
  }
  return const [];
}

int asInt(dynamic value, [int fallback = 0]) {
  if (value == null) return fallback;
  if (value is int) return value;
  if (value is num) return value.toInt();
  return int.tryParse(value.toString()) ?? fallback;
}

double asDouble(dynamic value, [double fallback = 0]) {
  if (value == null) return fallback;
  if (value is num) return value.toDouble();
  return double.tryParse(value.toString()) ?? fallback;
}
