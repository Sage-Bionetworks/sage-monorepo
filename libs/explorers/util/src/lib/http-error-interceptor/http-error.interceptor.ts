import { inject } from '@angular/core';
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';
// import { RollbarService } from '../services';
import { MessageService } from 'primeng/api';
import { TOAST_DURATION_MS } from '@sagebionetworks/explorers/constants';

export const httpErrorInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn,
): Observable<any> => {
  const messageService = inject(MessageService);
  // const rollbar = inject(RollbarService);

  return next(req).pipe(
    retry(1),
    catchError((error: HttpErrorResponse) => {
      let errorMessage = '';
      let errorSummary = '';
      let errorDetail = '';
      let tmpError = '';

      if (error.error instanceof ErrorEvent) {
        // client-side error
        errorDetail = `Error: ${error.error.message}`;
        errorSummary = 'Error';
      } else {
        // server-side error
        errorSummary = `Error Code: ${error.status}`;
        tmpError = `Message: ${error.message}`;
        const tmpSlashArray = tmpError.split('/');
        if (tmpSlashArray.length > 0) {
          const tmpString = tmpSlashArray[tmpSlashArray.length - 1];
          const tmpSpaceArray: string[] = tmpString.split(' ');
          const finalString = tmpSpaceArray[0].slice(0, -1);
          if (tmpSpaceArray.length > 0) {
            tmpError = `Message: ${finalString} not found!`;
          }
        }
        errorDetail = tmpError;
      }
      errorMessage = errorMessage + '\n' + errorSummary;

      // Displays error message on screen
      messageService.clear();
      messageService.add({
        severity: 'warn',
        sticky: true,
        summary: errorSummary,
        detail: errorDetail,
        life: TOAST_DURATION_MS,
      });
      setTimeout(() => {
        messageService.clear();
      }, TOAST_DURATION_MS);

      // TODO implement Rollbar service
      // https://sagebionetworks.jira.com/browse/MG-230
      // Send the error message to Rollbar
      // rollbar.error(error);

      return throwError(() => new Error(errorSummary, { cause: errorMessage }));
    }),
  );
};
