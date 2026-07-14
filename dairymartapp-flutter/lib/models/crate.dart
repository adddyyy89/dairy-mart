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
    final user = json['user'] as Map<String, dynamic>?;
    return CrateRecord(
      userId: json['userId'] ?? 0,
      crateCount: json['crateCount'] ?? 0,
      crateReceived: json['crateReceived'] ?? 0,
      crateReturned: json['crateReturned'] ?? 0,
      recordedAt: DateTime.tryParse(json['recordTimestamp']?.toString() ?? '') ??
          DateTime.now(),
      holderName: user != null
          ? '${user['firstName'] ?? ''} ${user['lastName'] ?? ''}'.trim()
          : null,
    );
  }

  Map<String, dynamic> toUpdateJson() => {
        'userId': userId,
        'crateCount': crateCount,
        'crateReceived': crateReceived,
        'crateReturned': crateReturned,
      };
}
