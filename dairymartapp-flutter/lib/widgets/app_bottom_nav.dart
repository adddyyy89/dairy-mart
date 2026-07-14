import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

/// Direct port of res/layout/bottom_navigation_bar.xml - same 5 destinations
/// in the same order: Dashboard, Orders, Deliveries, Crates, Ledger.
/// Reused by every salesman screen so navigation state is consistent no
/// matter which screen the user is currently on.
class AppBottomNav extends StatelessWidget {
  final int currentIndex;
  final ValueChanged<int> onTap;

  const AppBottomNav({
    super.key,
    required this.currentIndex,
    required this.onTap,
  });

  static const _items = [
    _NavItem(icon: Icons.home_rounded, label: 'DASHBOARD'),
    _NavItem(icon: Icons.receipt_long_rounded, label: 'ORDERS'),
    _NavItem(icon: Icons.local_shipping_rounded, label: 'DELIVERIES'),
    _NavItem(icon: Icons.inventory_2_rounded, label: 'CRATES'),
    _NavItem(icon: Icons.account_balance_wallet_rounded, label: 'LEDGER'),
  ];

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 72,
      decoration: const BoxDecoration(
        color: AppColors.surface,
        boxShadow: [
          BoxShadow(color: Color(0x1A000000), blurRadius: 8, offset: Offset(0, -2)),
        ],
      ),
      child: Row(
        children: List.generate(_items.length, (i) {
          final selected = i == currentIndex;
          final item = _items[i];
          return Expanded(
            child: InkWell(
              onTap: () => onTap(i),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    item.icon,
                    size: 24,
                    color: selected ? AppColors.primary : AppColors.textMuted,
                  ),
                  const SizedBox(height: 4),
                  Text(
                    item.label,
                    style: TextStyle(
                      fontSize: 10,
                      fontWeight: FontWeight.w600,
                      color: selected ? AppColors.primary : AppColors.textMuted,
                    ),
                  ),
                ],
              ),
            ),
          );
        }),
      ),
    );
  }
}

class _NavItem {
  final IconData icon;
  final String label;
  const _NavItem({required this.icon, required this.label});
}
