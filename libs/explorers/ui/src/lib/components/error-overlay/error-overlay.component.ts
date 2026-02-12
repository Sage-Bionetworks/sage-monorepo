import { Component, inject } from '@angular/core';
import { ErrorOverlayService } from '@sagebionetworks/explorers/services';

@Component({
  selector: 'explorers-error-overlay',
  imports: [],
  templateUrl: './error-overlay.component.html',
  styleUrls: ['./error-overlay.component.scss'],
})
export class ErrorOverlayComponent {
  private readonly errorOverlayService = inject(ErrorOverlayService);

  hasActiveError = this.errorOverlayService.hasActiveError;
  activeErrorMessage = this.errorOverlayService.activeErrorMessage;

  onReload() {
    this.errorOverlayService.reloadPage();
  }
}
