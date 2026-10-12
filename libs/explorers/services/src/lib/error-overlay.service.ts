import { computed, Injectable, signal } from '@angular/core';
import { getAppUpdateMessage } from '@sagebionetworks/explorers/constants';

@Injectable({ providedIn: 'root' })
export class ErrorOverlayService {
  private errorMessage = signal<string | null>(null);
  private updateMessage = signal<string | null>(null);

  /**
   * The message to display, or null when the overlay is hidden. An app update notice takes
   * precedence over an error, since errors in a stale tab are most likely caused by the update.
   */
  activeMessage = computed(() => this.updateMessage() ?? this.errorMessage());

  isOverlayVisible = computed(() => this.activeMessage() !== null);

  showError(message: string) {
    // Only show if there's no active error - prevents cascading error messages
    if (this.errorMessage() === null) {
      this.errorMessage.set(message);
    }
  }

  showUpdateAvailable(siteName: string) {
    this.updateMessage.set(getAppUpdateMessage(siteName));
  }

  reloadPage() {
    window.location.reload();
  }
}
