import { Clipboard } from '@angular/cdk/clipboard';
import { Component, inject, input } from '@angular/core';
import { TooltipButtonComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-comparison-tool-share-url-button',
  imports: [TooltipButtonComponent],
  templateUrl: './comparison-tool-share-url-button.component.html',
  styleUrls: ['./comparison-tool-share-url-button.component.scss'],
})
export class ComparisonToolShareURLButtonComponent {
  tooltip = input('');

  private readonly clipboard = inject(Clipboard);

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
    return this.hasCopied
      ? 'URL copied to clipboard'
      : "Copy the URL to capture the table's current filtering, sorting, and pinned results";
  }
}
