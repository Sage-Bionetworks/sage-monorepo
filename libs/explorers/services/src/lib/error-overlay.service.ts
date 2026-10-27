import { computed, Injectable, signal } from '@angular/core';
import { getAppUpdateMessage, getDataUpdateMessage } from '@sagebionetworks/explorers/constants';

@Injectable({ providedIn: 'root' })
export class ErrorOverlayService {
  private readonly errorMessage = signal<string | null>(null);
  private readonly appUpdateMessage = signal<string | null>(null);
  private readonly dataUpdateMessage = signal<string | null>(null);

  /**
   * The message to display, or null when the overlay is hidden. An update notice takes precedence
   * over an error, since errors in a stale tab are most likely caused by the update. An app update
   * notice takes precedence over a data update notice: the reload both ask for fixes both, and new
   * data may need the new app to display it.
   */
  readonly activeMessage = computed(
    () => this.appUpdateMessage() ?? this.dataUpdateMessage() ?? this.errorMessage(),
  );

  readonly isOverlayVisible = computed(() => this.activeMessage() !== null);

  showError(message: string) {
    // Only show if there's no active error - prevents cascading error messages
    if (this.errorMessage() === null) {
      this.errorMessage.set(message);
    }
  }

  showAppUpdateAvailable(siteName: string) {
    this.appUpdateMessage.set(getAppUpdateMessage(siteName));
  }

  showDataUpdateAvailable(siteName: string) {
    this.dataUpdateMessage.set(getDataUpdateMessage(siteName));
  }

  reloadPage() {
    window.location.reload();
  }
}
