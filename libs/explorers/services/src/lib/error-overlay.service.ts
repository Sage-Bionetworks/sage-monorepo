import { computed, Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ErrorOverlayService {
  /** Signal containing the current error message, or null if no error */
  private errorMessage = signal<string | null>(null);

  /** Signal indicating whether an error overlay is currently visible */
  hasActiveError = computed(() => this.errorMessage() !== null);

  /** The current error message to display */
  activeErrorMessage = computed(() => this.errorMessage() ?? 'Something went wrong.');

  showError(message: string) {
    // Only show if there's no active error - prevents cascading error messages
    if (this.errorMessage() === null) {
      this.errorMessage.set(message);
    }
  }

  reloadPage() {
    window.location.reload();
  }
}
