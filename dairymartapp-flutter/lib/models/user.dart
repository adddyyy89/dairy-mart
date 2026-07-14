/// Maps to the `role` field returned by POST /auth/login and the
/// `userTypeId` used everywhere else in the backend.
/// Source: MainActivity.java login callback -
///   role == 1 -> Admin, role == 2 -> Salesman, role == 3 -> Retailer
enum UserRole { admin, salesman, retailer, unknown }

UserRole userRoleFromId(int id) {
  switch (id) {
    case 1:
      return UserRole.admin;
    case 2:
      return UserRole.salesman;
    case 3:
      return UserRole.retailer;
    default:
      return UserRole.unknown;
  }
}

int userRoleToId(UserRole role) {
  switch (role) {
    case UserRole.admin:
      return 1;
    case UserRole.salesman:
      return 2;
    case UserRole.retailer:
      return 3;
    case UserRole.unknown:
      return 0;
  }
}

/// The logged-in session, persisted via SharedPreferences the same way the
/// legacy Android app persisted "dairymart" prefs (userid, usertypeid, auth).
class AuthSession {
  final int userId;
  final UserRole role;
  final String phoneNumber;
  final String basicAuthHeader; // "Basic base64(phone:password)"

  const AuthSession({
    required this.userId,
    required this.role,
    required this.phoneNumber,
    required this.basicAuthHeader,
  });
}

/// Maps to UserDTO from GET /user/get/{id}
class AppUser {
  final int userId;
  final String phoneNumber;
  final String firstName;
  final String? lastName;
  final int userTypeId;
  final String? userTypeDesc;
  final String? emailId;
  final int crateCount;
  final bool isActive;

  AppUser({
    required this.userId,
    required this.phoneNumber,
    required this.firstName,
    this.lastName,
    required this.userTypeId,
    this.userTypeDesc,
    this.emailId,
    this.crateCount = 0,
    this.isActive = true,
  });

  String get displayName =>
      lastName != null && lastName!.isNotEmpty ? '$firstName $lastName' : firstName;

  factory AppUser.fromJson(Map<String, dynamic> json) {
    final type = json['type'] as Map<String, dynamic>?;
    return AppUser(
      userId: json['userId'] ?? 0,
      phoneNumber: json['phoneNumber'] ?? '',
      firstName: json['firstName'] ?? '',
      lastName: json['lastName'],
      userTypeId: json['userTypeId'] ?? 0,
      userTypeDesc: type?['userTypeDesc'],
      emailId: json['emailId'],
      crateCount: json['crateCount'] ?? 0,
      isActive: json['isActive'] ?? true,
    );
  }
}
