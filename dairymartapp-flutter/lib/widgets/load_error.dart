import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

class LoadError extends StatelessWidget {
  final String message;
  final VoidCallback onRetry;

  const LoadError({super.key, required this.message, required this.onRetry});

  @override
  Widget build(BuildContext context) {
    final serverDown = message.toLowerCase().contains('server is down');
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              serverDown ? Icons.cloud_off_outlined : Icons.error_outline,
              color: AppColors.danger,
              size: 40,
            ),
            const SizedBox(height: 12),
            Text(
              serverDown ? 'The server is down' : 'Something went wrong',
              textAlign: TextAlign.center,
              style: const TextStyle(
                fontWeight: FontWeight.w700,
                fontSize: 16,
              ),
            ),
            const SizedBox(height: 8),
            Text(message,
                textAlign: TextAlign.center,
                style: const TextStyle(color: AppColors.textSecondary)),
            const SizedBox(height: 16),
            FilledButton(onPressed: onRetry, child: const Text('Retry')),
          ],
        ),
      ),
    );
  }
}
