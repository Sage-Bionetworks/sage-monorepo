import { Clipboard } from '@angular/cdk/clipboard';
import { Component, computed, inject } from '@angular/core';
import {
  getShareUrlTooltip,
  TABLE_DATA_LOADING_MESSAGE,
} from '@sagebionetworks/explorers/constants';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { TooltipButtonComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-comparison-tool-share-url-button',
  imports: [TooltipButtonComponent],
  templateUrl: './comparison-tool-share-url-button.component.html',
  styleUrls: ['./comparison-tool-share-url-button.component.scss'],
})
export class ComparisonToolShareURLButtonComponent {
  private readonly clipboard = inject(Clipboard);
  private readonly comparisonToolService = inject(ComparisonToolService);

  disabled = computed(
    () =>
      this.comparisonToolService.isLoadingTableData() ||
      this.comparisonToolService.pinnedFetchFailed(),
  );

  private hasCopied = false;
  private timeoutId?: ReturnType<typeof setTimeout>;

  copyUrl = () => {
    this.clipboard.copy(window.location.href);
    this.hasCopied = true;

    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
    }

    this.timeoutId = setTimeout(() => {
      this.hasCopied = false;
    }, 5000);
  };

  getTooltipText(): string {
    if (this.hasCopied) return 'URL copied to clipboard';
    return this.comparisonToolService.isLoadingTableData()
      ? TABLE_DATA_LOADING_MESSAGE
      : getShareUrlTooltip(this.comparisonToolService.nouns());
  }
}
