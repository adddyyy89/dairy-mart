import 'package:flutter_test/flutter_test.dart';
import 'package:dairymart/main.dart';

void main() {
  testWidgets('app launches', (WidgetTester tester) async {
    await tester.pumpWidget(const DairyMartApp());
    await tester.pump();
    expect(find.text('Dairy Mart'), findsWidgets);
  });
}
