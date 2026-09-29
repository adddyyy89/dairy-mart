import '../utils/json_unwrap.dart';

class AppNotification {
  final int notificationId;
  final int userId;
  final String kind;
  final String title;
  final String message;
  final int refId;
  final bool isRead;
  final DateTime? createdOn;

  AppNotification({
    required this.notificationId,
    required this.userId,
    required this.kind,
    required this.title,
    required this.message,
    required this.refId,
    required this.isRead,
    this.createdOn,
  });

  factory AppNotification.fromJson(Map<String, dynamic> json) {
    return AppNotification(
      notificationId: asInt(json['notificationId']),
      userId: asInt(json['userId']),
      kind: json['kind']?.toString() ?? '',
      title: json['title']?.toString() ?? '',
      message: json['message']?.toString() ?? '',
      refId: asInt(json['refId']),
      isRead: json['isRead'] == true || json['read'] == true,
      createdOn: _parseDate(json['createdOn']),
    );
  }

  static DateTime? _parseDate(dynamic value) {
    if (value == null) return null;
    return DateTime.tryParse(value.toString());
  }
}
