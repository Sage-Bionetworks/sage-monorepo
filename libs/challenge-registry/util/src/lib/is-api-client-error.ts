import { BasicError as ApiClientError } from '@sagebionetworks/challenge-registry/api-client-angular';

// Type guard for ApiClientError
export function isApiClientError(error: any): error is ApiClientError {
  const err = error as ApiClientError;
  return err.status !== undefined && err.title !== undefined;
}
