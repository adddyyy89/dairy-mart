import 'package:flutter/services.dart';

const phoneNumberMaxLength = 10;

final phoneNumberFormatters = <TextInputFormatter>[
  FilteringTextInputFormatter.digitsOnly,
  LengthLimitingTextInputFormatter(phoneNumberMaxLength),
];

bool isValidPhoneNumber(String value) =>
    RegExp(r'^\d{10}$').hasMatch(value.trim());
