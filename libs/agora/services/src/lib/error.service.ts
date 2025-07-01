// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ErrorHandler, inject, Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';

// -------------------------------------------------------------------------- //
// Service
// -------------------------------------------------------------------------- //
@Injectable({
  providedIn: 'root',
})
export class ErrorService implements ErrorHandler {
  private readonly messageService = inject(MessageService);

  handleError(error: any) {
    this.messageService.clear();
    this.messageService.add({
      severity: 'warn',
      sticky: true,
      summary: 'Error',
      detail: 'An unexpected error has occurred, we recommend reloading the application.',
    });
    setTimeout(() => {
      this.messageService.clear();
    }, 5000);

    const getCircularReplacer = () => {
      const seen = new WeakSet();
      return (key: string, value: string) => {
        if (typeof value === 'object' && value !== null) {
          if (seen.has(value)) {
            return;
          }
          seen.add(value);
        }
        return value;
      };
    };

    console.error(JSON.stringify(error, getCircularReplacer()));
    throw error;
  }
}
