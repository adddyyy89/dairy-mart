import '../services/api_client.dart';

String apiErrorMessage(Object error) {
  if (error is ApiException) {
    if (error.message.trim().isNotEmpty) {
      return error.message;
    }
    if (error.statusCode == 400) {
      return 'That request was not valid. Check the details and try again.';
    }
  }
  return 'The server is down. Please try again later.';
}
