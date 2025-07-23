import { inject, Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';
import { TOAST_DURATION_MS } from '@sagebionetworks/explorers/constants';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly messageService = inject(MessageService);

  showError(message: string) {
    this.messageService.clear();

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
}
