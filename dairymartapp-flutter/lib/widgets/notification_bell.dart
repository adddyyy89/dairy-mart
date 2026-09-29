import 'dart:async';

import 'package:flutter/material.dart';

import '../screens/notifications_screen.dart';
import '../services/notification_service.dart';
import '../services/session_manager.dart';
import '../theme/app_theme.dart';

class NotificationBell extends StatefulWidget {
  final Color? color;

  const NotificationBell({super.key, this.color});

  @override
  State<NotificationBell> createState() => _NotificationBellState();
}

class _NotificationBellState extends State<NotificationBell> {
  int _unread = 0;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _refresh();
    _timer = Timer.periodic(const Duration(seconds: 45), (_) => _refresh());
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  Future<void> _refresh() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    try {
      final count = await NotificationService.instance.unreadCount(session.userId);
      if (mounted) setState(() => _unread = count);
    } catch (_) {
      // Badge stays at last known count if the server is unreachable.
    }
  }

  Future<void> _open() async {
    await Navigator.of(context).push(
      MaterialPageRoute(builder: (_) => const NotificationsScreen()),
    );
    if (mounted) await _refresh();
  }

  @override
  Widget build(BuildContext context) {
    final color = widget.color ?? Colors.white;
    return IconButton(
      tooltip: 'Notifications',
      onPressed: _open,
      icon: Badge(
        isLabelVisible: _unread > 0,
        label: Text(_unread > 99 ? '99+' : '$_unread'),
        child: Icon(Icons.notifications_outlined, color: color),
      ),
    );
  }
}

class NotificationBellTinted extends StatelessWidget {
  const NotificationBellTinted({super.key});

  @override
  Widget build(BuildContext context) {
    return const NotificationBell(color: AppColors.primary);
  }
}
