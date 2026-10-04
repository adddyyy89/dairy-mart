import '../utils/json_unwrap.dart';

/// Maps to CrateDTO (dairyappserver/dto/CrateDTO.java)
class CrateRecord {
  final int userId;
  final int crateCount; // net crates currently held/assigned
  final int crateReceived;
  final int crateReturned;
  final DateTime recordedAt;
  final String? holderName; // enriched from UserDTO if present

  CrateRecord({
    required this.userId,
    required this.crateCount,
    required this.crateReceived,
    required this.crateReturned,
    required this.recordedAt,
    this.holderName,
  });

  /// "Engaged" crates = handed out to retailers and not yet returned.
  int get engaged => crateReceived - crateReturned;

  factory CrateRecord.fromJson(Map<String, dynamic> json) {
    final user = asMap(json['user']);
    return CrateRecord(
      userId: asInt(json['userId']),
      crateCount: asInt(json['crateCount']),
      crateReceived: asInt(json['crateReceived']),
      crateReturned: asInt(json['crateReturned']),
      recordedAt: DateTime.tryParse(json['recordTimestamp']?.toString() ?? '') ??
          DateTime.now(),
      holderName: user.isEmpty
          ? null
          : '${user['firstName'] ?? ''} ${user['lastName'] ?? ''}'.trim(),
    );
  }

  Map<String, dynamic> toUpdateJson() => {
        'userId': userId,
        'crateCount': crateCount,
        'crateReceived': crateReceived,
        'crateReturned': crateReturned,
      };
}
