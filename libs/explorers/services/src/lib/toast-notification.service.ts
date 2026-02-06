import { inject, Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';

const TOAST_DURATION_MS = 5000;

@Injectable({ providedIn: 'root' })
export class ToastNotificationService {
  private readonly messageService = inject(MessageService);

  showError(message: string) {
    this.messageService.add({
      severity: 'error',
      sticky: true,
      summary: 'Error',
      detail: message,
    });

    setTimeout(() => {
      this.messageService.clear();
    }, TOAST_DURATION_MS);
  }

  showWarning(message: string) {
    this.messageService.add({
      severity: 'warn',
      summary: 'Warning',
      detail: message,
    });

    setTimeout(() => {
      this.messageService.clear();
    }, TOAST_DURATION_MS);
  }

  showSuccess(message: string) {
    this.messageService.add({
      severity: 'success',
      summary: 'Success',
      detail: message,
    });

    setTimeout(() => {
      this.messageService.clear();
    }, TOAST_DURATION_MS);
  }

  showInfo(message: string) {
    this.messageService.add({
      severity: 'info',
      summary: 'Info',
      detail: message,
    });

    setTimeout(() => {
      this.messageService.clear();
    }, TOAST_DURATION_MS);
  }
}
