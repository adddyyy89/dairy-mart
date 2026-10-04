import 'package:flutter/material.dart';

import '../models/app_notification.dart';
import '../services/notification_service.dart';
import '../services/session_manager.dart';
import '../theme/app_theme.dart';
import '../utils/api_error.dart';
import '../widgets/load_error.dart';

class NotificationsScreen extends StatefulWidget {
  const NotificationsScreen({super.key});

  @override
  State<NotificationsScreen> createState() => _NotificationsScreenState();
}

class _NotificationsScreenState extends State<NotificationsScreen> {
  bool _loading = true;
  String? _error;
  List<AppNotification> _items = [];

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final items = await NotificationService.instance.listForUser(session.userId);
      if (mounted) setState(() => _items = items);
    } catch (e) {
      if (mounted) setState(() => _error = apiErrorMessage(e));
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _open(AppNotification item) async {
    if (!item.isRead) {
      try {
        await NotificationService.instance.markRead(item.notificationId);
      } catch (_) {}
    }
    if (mounted) await _load();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Notifications')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? LoadError(message: _error!, onRetry: _load)
              : RefreshIndicator(
                  onRefresh: _load,
                  child: _items.isEmpty
                      ? ListView(
                          children: const [
                            SizedBox(height: 120),
                            Center(
                              child: Text('No notifications yet.',
                                  style: TextStyle(color: AppColors.textSecondary)),
                            ),
                          ],
                        )
                      : ListView.separated(
                          itemCount: _items.length,
                          separatorBuilder: (_, __) => const Divider(height: 1),
                          itemBuilder: (context, index) {
                            final item = _items[index];
                            final unread = !item.isRead;
                            return ListTile(
                              leading: CircleAvatar(
                                backgroundColor: unread
                                    ? AppColors.primary.withValues(alpha: 0.12)
                                    : AppColors.surfaceMuted,
                                child: Icon(
                                  item.kind == 'LEDGER'
                                      ? Icons.account_balance_wallet_outlined
                                      : Icons.receipt_long_outlined,
                                  color: unread
                                      ? AppColors.primary
                                      : AppColors.textMuted,
                                ),
                              ),
                              title: Text(
                                item.title,
                                style: TextStyle(
                                  fontWeight:
                                      unread ? FontWeight.w700 : FontWeight.w500,
                                ),
                              ),
                              subtitle: Text(item.message),
                              trailing: unread
                                  ? const Icon(Icons.circle,
                                      size: 10, color: AppColors.primary)
                                  : null,
                              onTap: () => _open(item),
                            );
                          },
                        ),
                ),
    );
  }
}
