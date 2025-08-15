import { Component, computed, input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { isExternalLink } from '@sagebionetworks/shared/util';

@Component({
  selector: 'explorers-comparison-tool-table-link',
  imports: [RouterModule],
  templateUrl: './comparison-tool-table-link.component.html',
  styleUrls: ['./comparison-tool-table-link.component.scss'],
})
export class ComparisonToolTableLinkComponent {
  linkText = input<string>('');
  linkUrl = input<string>('');

  internalLinkUrl = computed(() => {
    const url = this.linkUrl().trim();

    // Return URL as-is if it already starts with '/'
    if (url.startsWith('/')) {
      return url;
    }

    // Prepend '/' to make it a proper internal route
    return `/${url}`;
  });

  isExternalLink(): boolean {
    const url = this.linkUrl();
    return url ? isExternalLink(url) : false;
  }
}
