import { Component, ViewEncapsulation, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { getOverwritePinsMessage } from '@sagebionetworks/explorers/constants';
import { ComparisonToolService, PlatformService } from '@sagebionetworks/explorers/services';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DialogModule } from 'primeng/dialog';
import { OVERWRITE_PINS_DIALOG } from './overwrite-pins-dialog.constants';

/**
 * Asks the user to confirm a pin edit that would drop the pins the current view can't show. Opens
 * while the service holds such an edit, and closing it any other way than confirming cancels it.
 */
@Component({
  selector: 'explorers-overwrite-pins-dialog',
  imports: [ButtonModule, CheckboxModule, DialogModule, FormsModule],
  templateUrl: './overwrite-pins-dialog.component.html',
  styleUrls: ['./overwrite-pins-dialog.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class OverwritePinsDialogComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);
  protected readonly platformService = inject(PlatformService);

  protected readonly copy = OVERWRITE_PINS_DIALOG;
  protected readonly isVisible = this.comparisonToolService.isOverwriteConfirmationVisible;
  // Applied only on confirm, since cancelling means the user still wants to be asked
  protected readonly willHideConfirmation = signal(false);

  protected readonly message = computed(() =>
    getOverwritePinsMessage(
      {
        hiddenPinCount: this.comparisonToolService.hiddenPinCount() ?? 0,
        hiddenParentPinCount: this.comparisonToolService.parentIdDataKey()
          ? (this.comparisonToolService.hiddenParentPinCount() ?? 0)
          : null,
      },
      this.comparisonToolService.pinnedItemsView() ?? [],
      this.comparisonToolService.nouns(),
    ),
  );

  confirm() {
    if (this.willHideConfirmation()) {
      this.comparisonToolService.setOverwriteConfirmationHiddenByUser(true);
    }
    this.willHideConfirmation.set(false);
    this.comparisonToolService.confirmPinEdit();
  }

  cancel() {
    this.willHideConfirmation.set(false);
    this.comparisonToolService.cancelPinEdit();
  }

  // The close button, the mask, and Escape all hide the dialog without confirming
  onVisibleChange(isVisible: boolean) {
    if (!isVisible && this.isVisible()) {
      this.cancel();
    }
  }
}
