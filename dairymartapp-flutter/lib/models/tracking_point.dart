import '../utils/json_unwrap.dart';

class TrackingPoint {
  final int trackId;
  final int userId;
  final double latitude;
  final double longitude;
  final bool active;
  final String timestamp;

  const TrackingPoint({
    required this.trackId,
    required this.userId,
    required this.latitude,
    required this.longitude,
    required this.active,
    required this.timestamp,
  });

  factory TrackingPoint.fromJson(Map<String, dynamic> raw) {
    final json = asMap(raw);
    return TrackingPoint(
      trackId: asInt(json['trackId']),
      userId: asInt(json['userId']),
      latitude: asDouble(json['latitude']),
      longitude: asDouble(json['longitude']),
      active: json['active'] == true || json['isActive'] == true,
      timestamp: json['timestamp']?.toString() ?? '',
    );
  }
}
