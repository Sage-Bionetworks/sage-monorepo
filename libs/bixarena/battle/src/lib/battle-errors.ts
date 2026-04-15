// Typed error thrown by BattleStreamService when the HTTP response is not OK
export class StreamHttpError extends Error {
  constructor(public readonly status: number) {
    super(`Stream request failed: ${status}`);
  }
}

// Map HTTP status codes to user-facing error messages and retry eligibility
export function mapStreamHttpError(status: number): {
  message: string;
  retryable: boolean;
} {
  switch (status) {
    case 400:
      return {
        message: 'This question could not be processed. Try a new question',
        retryable: false,
      };
    case 401:
      return { message: 'Please log in to continue', retryable: false };
    case 403:
      return { message: 'Please log in to continue', retryable: false };
    case 404:
      return { message: 'This battle is no longer available. Try a new battle', retryable: false };
    case 429:
      return { message: 'Too many requests — please wait a moment', retryable: true };
    case 500:
      return { message: 'Something went wrong', retryable: true };
    default:
      return { message: 'Something went wrong', retryable: true };
  }
}
