import 'package:flutter/material.dart';
import 'models/user.dart';
import 'screens/admin/admin_dashboard_screen.dart';
import 'screens/auth/login_screen.dart';
import 'screens/retailer/retailer_dashboard_screen.dart';
import 'screens/salesman/salesman_dashboard_screen.dart';
import 'services/session_manager.dart';
import 'theme/app_theme.dart';

void main() {
  runApp(const DairyMartApp());
}

class DairyMartApp extends StatelessWidget {
  const DairyMartApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Dairy Mart',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.light(),
      home: const _SplashRouter(),
    );
  }
}

/// Checks for a persisted session (mirrors the legacy app reading its
/// "dairymart" SharedPreferences on every *DashboardActivity.onCreate) and
/// routes straight to the right role dashboard, skipping login if still
/// signed in.
class _SplashRouter extends StatefulWidget {
  const _SplashRouter();

  @override
  State<_SplashRouter> createState() => _SplashRouterState();
}

class _SplashRouterState extends State<_SplashRouter> {
  @override
  void initState() {
    super.initState();
    _resolveSession();
  }

  Future<void> _resolveSession() async {
    final session = await SessionManager.instance.load();
    if (!mounted) return;

    if (session == null) {
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => const LoginScreen()),
      );
      return;
    }

    final Widget destination = switch (session.role) {
      UserRole.admin => const AdminDashboardScreen(),
      UserRole.salesman => const SalesmanDashboardScreen(),
      UserRole.retailer => const RetailerDashboardScreen(),
      UserRole.unknown => const LoginScreen(),
    };

    Navigator.of(context)
        .pushReplacement(MaterialPageRoute(builder: (_) => destination));
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      backgroundColor: AppColors.primary,
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'Dairy Mart',
              style: TextStyle(
                color: Colors.white,
                fontSize: 32,
                fontWeight: FontWeight.w900,
              ),
            ),
            SizedBox(height: 24),
            CircularProgressIndicator(color: Colors.white),
          ],
        ),
      ),
    );
  }
}
