import { afterRenderEffect, Component, ElementRef, inject, viewChild } from '@angular/core';
import { ErrorOverlayService } from '@sagebionetworks/explorers/services';

/**
 * Blocks the page until the user reloads it. Opened as a modal `<dialog>`, so the browser makes
 * the rest of the page inert, moves focus to the reload button, and announces it as a dialog. The
 * Escape key and other close requests are refused, since the page behind it can no longer be used.
 */
@Component({
  selector: 'explorers-error-overlay',
  imports: [],
  templateUrl: './error-overlay.component.html',
  styleUrls: ['./error-overlay.component.scss'],
})
export class ErrorOverlayComponent {
  private readonly errorOverlayService = inject(ErrorOverlayService);
  private readonly dialog = viewChild<ElementRef<HTMLDialogElement>>('dialog');

  isOverlayVisible = this.errorOverlayService.isOverlayVisible;
  activeMessage = this.errorOverlayService.activeMessage;

  constructor() {
    // Runs only in the browser, which is the only place a dialog can be opened as a modal
    afterRenderEffect(() => {
      const dialog = this.dialog()?.nativeElement;
      if (dialog && !dialog.open) {
        dialog.showModal();
      }
    });
  }

  onReload() {
    this.errorOverlayService.reloadPage();
  }
}
