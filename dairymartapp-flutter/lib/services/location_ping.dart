import 'dart:async';

import 'package:geolocator/geolocator.dart';

import '../models/user.dart';
import 'session_manager.dart';
import 'tracking_service.dart';

/// Sends GPS points to POST /tracking/update about once a minute while the
/// salesman app is in the foreground.
class LocationPing {
  LocationPing._();
  static final LocationPing instance = LocationPing._();

  StreamSubscription<Position>? _sub;
  DateTime? _lastSent;

  Future<void> start() async {
    final session = SessionManager.instance.current;
    if (session == null || session.role != UserRole.salesman) return;
    if (_sub != null) return;

    var permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
    }
    if (permission == LocationPermission.denied ||
        permission == LocationPermission.deniedForever) {
      return;
    }

    final enabled = await Geolocator.isLocationServiceEnabled();
    if (!enabled) return;

    _sub = Geolocator.getPositionStream(
      locationSettings: const LocationSettings(
        accuracy: LocationAccuracy.high,
        distanceFilter: 25,
      ),
    ).listen((position) {
      final now = DateTime.now();
      if (_lastSent != null && now.difference(_lastSent!).inSeconds < 60) {
        return;
      }
      _lastSent = now;
      final userId = SessionManager.instance.current?.userId;
      if (userId == null) return;
      TrackingService.instance.ping(
        userId: userId,
        latitude: position.latitude,
        longitude: position.longitude,
      );
    });
  }

  Future<void> stop() async {
    await _sub?.cancel();
    _sub = null;
  }
}
