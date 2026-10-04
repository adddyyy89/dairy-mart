import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

/// Design tokens pulled directly from the legacy Android app's DESIGN.md
/// and the XML layouts under res/layout (activity_main.xml,
/// activity_salesman_dashboard_2.xml, activity_salesman_activity_orders.xml).
///
/// Keeping these as named constants means every screen we port stays
/// visually consistent, and re-theming later is a one-file change.
class AppColors {
  AppColors._();

  // Primary brand color - buttons, active nav, links, accents
  static const Color primary = Color(0xFF1A73E8);
  static const Color primaryLight = Color(0xFFE8F0FE); // active stat card bg
  static const Color primarySubtle = Color(0x401A73E8);

  // Neutral surfaces
  static const Color background = Color(0xFFF8F9FA);
  static const Color surface = Colors.white;
  static const Color surfaceMuted = Color(0xFFF1F3F4);
  static const Color border = Color(0xFFE0E0E0);

  // Text
  static const Color textPrimary = Color(0xFF202124);
  static const Color textSecondary = Color(0xFF5F6368);
  static const Color textMuted = Color(0xFF9AA0A6);

  // Semantic
  static const Color success = Color(0xFF388E3C);
  static const Color danger = Color(0xFFD32F2F);
  static const Color warning = Color(0xFFF9A825);

  // One distinct color per real order status (public.orderstatus:
  // NEW, CONFIRMED, REJECTED, DISPATCHED, DELIVERED, RETURNED, CANCELLED) -
  // each status gets its own color rather than being grouped under a shared
  // one, so the chips are visually distinguishable at a glance.
  static const Color statusNew = Color(0xFF1E88E5); // blue
  static const Color statusConfirmed = Color(0xFF5E35B1); // indigo/purple
  static const Color statusRejected = Color(0xFFE53935); // red
  static const Color statusDispatched = Color(0xFFFB8C00); // orange
  static const Color statusDelivered = Color(0xFF43A047); // green
  static const Color statusReturned = Color(0xFFFFA000); // amber
  static const Color statusCancelled = Color(0xFF616161); // neutral grey

  // Order status colors (used across order cards / chips).
  // Matches public.orderstatus exactly: NEW, CONFIRMED, REJECTED,
  // DISPATCHED, DELIVERED, RETURNED, CANCELLED.
  static Color statusColor(String status) {
    switch (status.toUpperCase()) {
      case 'NEW':
        return statusNew;
      case 'CONFIRMED':
        return statusConfirmed;
      case 'REJECTED':
        return statusRejected;
      case 'DISPATCHED':
        return statusDispatched;
      case 'DELIVERED':
      case 'COMPLETED':
        return statusDelivered;
      case 'RETURNED':
        return statusReturned;
      case 'CANCELLED':
        return statusCancelled;
      // Legacy fallbacks from before the real status list was confirmed -
      // kept in case any stale data still uses these.
      case 'PENDING':
      case 'IN PROGRESS':
        return primary;
      default:
        return textMuted;
    }
  }
}

class AppRadius {
  AppRadius._();
  static const double sm = 8;
  static const double md = 12;
  static const double lg = 16;
  static const double xl = 24;
  static const double pill = 28;
}

class AppSpacing {
  AppSpacing._();
  static const double xs = 4;
  static const double sm = 8;
  static const double md = 16;
  static const double lg = 24;
  static const double xl = 32;
}

class AppTheme {
  AppTheme._();

  static ThemeData light() {
    final base = ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.fromSeed(
        seedColor: AppColors.primary,
        primary: AppColors.primary,
        surface: AppColors.surface,
        brightness: Brightness.light,
      ),
      scaffoldBackgroundColor: AppColors.background,
      fontFamily: GoogleFonts.inter().fontFamily,
      textTheme: GoogleFonts.interTextTheme(),
    );

    return base.copyWith(
      appBarTheme: const AppBarTheme(
        backgroundColor: AppColors.background,
        foregroundColor: AppColors.textPrimary,
        elevation: 0,
        centerTitle: false,
        titleTextStyle: TextStyle(
          color: AppColors.primary,
          fontSize: 22,
          fontWeight: FontWeight.w700,
        ),
      ),
      cardTheme: CardThemeData(
        color: AppColors.surface,
        elevation: 0,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AppRadius.md),
          side: const BorderSide(color: AppColors.border, width: 1),
        ),
        margin: EdgeInsets.zero,
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
          minimumSize: const Size.fromHeight(56),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(AppRadius.pill),
          ),
          textStyle: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: AppColors.surface,
        contentPadding:
            const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(AppRadius.md),
          borderSide: const BorderSide(color: AppColors.border),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(AppRadius.md),
          borderSide: const BorderSide(color: AppColors.border),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(AppRadius.md),
          borderSide: const BorderSide(color: AppColors.primary, width: 1.5),
        ),
      ),
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        backgroundColor: AppColors.surface,
        selectedItemColor: AppColors.primary,
        unselectedItemColor: AppColors.textMuted,
        type: BottomNavigationBarType.fixed,
        showUnselectedLabels: true,
        elevation: 16,
      ),
    );
  }
}