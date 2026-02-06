import { inject, Injectable, signal } from '@angular/core';
import { LoggerService } from './logger.service';

@Injectable({ providedIn: 'root' })
export class ErrorOverlayService {
  private readonly logger = inject(LoggerService);

  /** Signal indicating whether an error overlay is currently visible */
  hasActiveError = signal(false);

  showError(message: string) {
    this.hasActiveError.set(true);
    this.logger.error(message);
  }

  reloadPage() {
    window.location.reload();
  }
}
