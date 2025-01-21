import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { retry, catchError } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';
import { RollbarService } from './rollbar.service';
import { MessageService } from 'primeng/api';
import { inject } from '@angular/core';

export const httpErrorInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn,
): Observable<any> => {
  const rollbar = inject(RollbarService);
  const messageService = inject(MessageService);

  return next(req).pipe(
    retry(1),
    catchError((error: HttpErrorResponse) => {
      let errorMessage = '';
      let errorSummary = '';
      let errorDetail = '';
      let tmpError = '';

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorDetail = `Error: ${error.error.message}`;
        errorSummary = 'Error';
      } else {
        // Server-side error
        errorSummary = `Error Code: ${error.status}`;
        tmpError = `Message: ${error.message}`;
        const tmpSlashArray = tmpError.split('/');
        if (tmpSlashArray.length > 0) {
          // Extract the last part of the error message
          const tmpString = tmpSlashArray[tmpSlashArray.length - 1];
          const tmpSpaceArray: string[] = tmpString.split(' ');
          const finalString = tmpSpaceArray[0].slice(0, -1);
          if (tmpSpaceArray.length > 0) {
            tmpError = `Message: Gene ${finalString} not found!`;
          }
        }
        errorDetail = tmpError;
      }
      errorMessage += '\n' + errorSummary;

      // Displays error message on screen
      messageService.clear();
      messageService.add({
        severity: 'warn',
        sticky: true,
        summary: errorSummary,
        detail: errorDetail,
        life: 3000,
      });

      setTimeout(() => {
        messageService.clear();
      }, 3000);

      // Send the error message to Rollbar
      rollbar.error(error);

      return throwError(() => ({ errorSummary, errorMessage }));
    }),
  );
};
