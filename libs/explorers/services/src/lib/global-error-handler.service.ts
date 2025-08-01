import { ErrorHandler, inject, Injectable } from '@angular/core';
import { LoggerService } from './logger.service';
import { NotificationService } from './notification.service';
import { AppError } from '@sagebionetworks/explorers/models';

@Injectable({
  providedIn: 'root',
})
export class GlobalErrorHandler implements ErrorHandler {
  private logger = inject(LoggerService);
  private notificationService = inject(NotificationService);

  handleError(error: any): void;
  handleError(message: string): void;
  handleError(message: string, error: any): void;
  handleError(arg1: any, arg2?: any): void {
    const arg1IsString = typeof arg1 === 'string';
    const message = arg1IsString ? arg1 : arg1?.message || 'An unexpected error has occurred.';
    const error = arg1IsString ? arg2 : arg1;

    this.logger.error(message, error);

    const notifyUser = this.isUserFacingError(error);
    if (notifyUser) {
      this.notificationService.showError(message);
    }
  }

  private isUserFacingError(error: any) {
    if (error instanceof AppError) {
      return error.isUserFacingError;
    }
    return false;
  }
}
