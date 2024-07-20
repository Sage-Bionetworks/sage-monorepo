import { Router } from '@angular/router';
import { isApiClientError } from './is-api-client-error';
import { HttpStatusRedirect } from './http-status-redirect';

export function handleHttpError(
  error: any,
  router: Router,
  httpStatusRedirect: HttpStatusRedirect,
  defaultRedirectUrl = '/',
): Error {
  const err = error.error;
  const status = error.status as number;
  const message = isApiClientError(err) ? err.detail : error.message;
  // redirect to corresponding url based on http status code
  // if no url is specified for the status code, redirect to default url
  router.navigate([httpStatusRedirect[status] || defaultRedirectUrl]);
  // return the error message
  return new Error(message);
}
