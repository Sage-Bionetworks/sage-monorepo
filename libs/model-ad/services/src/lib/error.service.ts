import { ErrorHandler, inject, Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';
import { TOAST_DURATION } from '@sagebionetworks/model-ad/config';

@Injectable({
  providedIn: 'root',
})
export class ErrorService implements ErrorHandler {
  private readonly messageService = inject(MessageService);

  handleError(error: any): void;
  handleError(message: string): void;
  handleError(message: string, error: any): void;
  handleError(arg1: any, arg2?: any): void {
    this.messageService.clear();
    let detail: string;
    if (typeof arg1 === 'string') {
      detail = arg1;
      if (arg2) {
        console.error(arg2);
      } else {
        console.error(detail);
      }
    } else {
      detail =
        arg1?.message ||
        'An unexpected error has occurred, we recommend reloading the application.';
      console.error(arg1);
    }
    this.messageService.add({
      severity: 'error',
      sticky: true,
      summary: 'Error',
      detail,
    });
    setTimeout(() => {
      this.messageService.clear();
    }, TOAST_DURATION);
  }
}
