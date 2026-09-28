import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../models/tracking_point.dart';
import '../../services/session_manager.dart';
import '../../services/tracking_service.dart';
import '../../theme/app_theme.dart';
import '../../widgets/load_error.dart';

class TrackingScreen extends StatefulWidget {
  final int? userId;
  const TrackingScreen({super.key, this.userId});

  @override
  State<TrackingScreen> createState() => _TrackingScreenState();
}

class _TrackingScreenState extends State<TrackingScreen> {
  bool _loading = true;
  String? _error;
  List<TrackingPoint> _points = [];

  int get _userId =>
      widget.userId ?? SessionManager.instance.current?.userId ?? 0;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final points = await TrackingService.instance.getForDay(userId: _userId);
      if (mounted) setState(() => _points = points);
    } catch (e) {
      if (mounted) setState(() => _error = 'Could not load today\'s route.');
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _openMaps(TrackingPoint point) async {
    final uri = Uri.parse(
        'https://www.google.com/maps/search/?api=1&query=${point.latitude},${point.longitude}');
    await launchUrl(uri, mode: LaunchMode.externalApplication);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Today\'s route · user $_userId')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? LoadError(message: _error!, onRetry: _load)
              : _points.isEmpty
                  ? const Center(
                      child: Text('No GPS points recorded today.',
                          style: TextStyle(color: AppColors.textMuted)))
                  : RefreshIndicator(
                      onRefresh: _load,
                      child: ListView.separated(
                        padding: const EdgeInsets.all(16),
                        itemCount: _points.length,
                        separatorBuilder: (_, __) => const SizedBox(height: 8),
                        itemBuilder: (context, i) {
                          final p = _points[i];
                          return Card(
                            child: ListTile(
                              leading: const Icon(Icons.place_outlined,
                                  color: AppColors.primary),
                              title: Text(
                                  '${p.latitude.toStringAsFixed(5)}, ${p.longitude.toStringAsFixed(5)}'),
                              subtitle: Text(p.timestamp),
                              trailing: const Icon(Icons.map_outlined),
                              onTap: () => _openMaps(p),
                            ),
                          );
                        },
                      ),
                    ),
    );
  }
}
