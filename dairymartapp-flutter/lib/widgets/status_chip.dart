import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

/// Small rounded pill used to show order/delivery status
/// (mirrors the "IN PROGRESS" chip on the salesman dashboard hero card).
class StatusChip extends StatelessWidget {
  final String label;
  final Color? color;

  const StatusChip({super.key, required this.label, this.color});

  @override
  Widget build(BuildContext context) {
    final c = color ?? AppColors.statusColor(label);
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: c.withValues(alpha: 0.12),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        label.toUpperCase(),
        style: TextStyle(
          fontSize: 10,
          fontWeight: FontWeight.bold,
          color: c,
          letterSpacing: 0.3,
        ),
      ),
    );
  }
}
