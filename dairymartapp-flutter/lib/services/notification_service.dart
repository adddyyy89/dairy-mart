import '../models/app_notification.dart';
import '../utils/json_unwrap.dart';
import 'api_client.dart';
import 'api_config.dart';

class NotificationService {
  NotificationService._();
  static final NotificationService instance = NotificationService._();

  Future<List<AppNotification>> listForUser(int userId) async {
    final json = await ApiClient.instance.get(ApiConfig.notifications(userId));
    return asList(json).map((e) => AppNotification.fromJson(asMap(e))).toList();
  }

  Future<int> unreadCount(int userId) async {
    final json = await ApiClient.instance.get(ApiConfig.notificationUnread(userId));
    return asInt(asMap(json)['unread']);
  }

  Future<void> markRead(int notificationId) async {
    await ApiClient.instance.post(ApiConfig.notificationRead(notificationId), body: {});
  }
}
