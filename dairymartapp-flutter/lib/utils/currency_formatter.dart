import 'package:intl/intl.dart';

final _currencyFormat = NumberFormat.currency(
  locale: 'en_IN',
  symbol: '\u20B9 ', // ₹
  decimalDigits: 2,
);

/// Matches the "₹ -2,494.00" / "₹ 8,614.00" style seen throughout the
/// ledger and dashboard layouts.
String formatCurrency(num amount) => _currencyFormat.format(amount);
