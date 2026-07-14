import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

/// Recreates the small bordered stat tiles used on both the salesman and
/// retailer dashboards ("Balance", "Orders Placed", "Crates Assigned", ...).
class StatCard extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;
  final Color? valueColor;
  final Color? iconBackground;

  const StatCard({
    super.key,
    required this.icon,
    required this.label,
    required this.value,
    this.valueColor,
    this.iconBackground,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(icon, size: 20, color: AppColors.primary),
            const SizedBox(height: 6),
            Text(
              label,
              style: const TextStyle(
                fontSize: 12,
                color: AppColors.textSecondary,
              ),
            ),
            const SizedBox(height: 2),
            Text(
              value,
              style: TextStyle(
                fontSize: 17,
                fontWeight: FontWeight.bold,
                color: valueColor ?? AppColors.textPrimary,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// A 2x2 (or 2xN) grid of [StatCard]s with the same spacing used across
/// dashboard screens.
class StatCardGrid extends StatelessWidget {
  final List<StatCard> cards;
  const StatCardGrid({super.key, required this.cards});

  @override
  Widget build(BuildContext context) {
    return GridView.count(
      crossAxisCount: 2,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      mainAxisSpacing: 12,
      crossAxisSpacing: 12,
      childAspectRatio: 1.3,
      children: cards,
    );
  }
}