import { BasicError as ApiClientError } from '@sagebionetworks/openchallenges/api-client';
import { isNotNullOrUndefined } from 'type-guards';
// Type guard for ApiClientError
export function isApiClientError(error: any): error is ApiClientError {
  const err = error as ApiClientError;
  return isNotNullOrUndefined(err) && err.status !== undefined && err.title !== undefined;
}
