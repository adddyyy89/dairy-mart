import 'package:intl/intl.dart';

import '../models/tracking_point.dart';
import '../utils/json_unwrap.dart';
import 'api_client.dart';
import 'api_config.dart';

class TrackingService {
  TrackingService._();
  static final TrackingService instance = TrackingService._();

  static final _stamp = DateFormat('dd-MM-yyyy HH:mm:ss');
  static final _day = DateFormat('dd-MM-yyyy');

  Future<void> ping({
    required int userId,
    required double latitude,
    required double longitude,
  }) async {
    await ApiClient.instance.post(
      ApiConfig.trackingUpdate,
      body: {
        'userId': userId,
        'latitude': latitude,
        'longitude': longitude,
        'active': true,
        'timestamp': _stamp.format(DateTime.now()),
      },
    );
  }

  Future<List<TrackingPoint>> getForDay({
    required int userId,
    DateTime? day,
  }) async {
    final date = _day.format(day ?? DateTime.now());
    final json = await ApiClient.instance.get(
      '/tracking/get',
      query: {'userId': '$userId', 'date': date},
    );
    return asList(json).map((e) => TrackingPoint.fromJson(asMap(e))).toList();
  }
}
