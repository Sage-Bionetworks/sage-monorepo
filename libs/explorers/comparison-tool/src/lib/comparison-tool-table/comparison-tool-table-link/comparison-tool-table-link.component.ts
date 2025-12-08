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
  linkUrl = input<string | string[]>('');

  private parsedUrl = computed(() => {
    const urlInput = this.linkUrl();

    // If it's an array, use it directly as path segments
    if (Array.isArray(urlInput)) {
      return { path: urlInput, queryParams: {} };
    }

    const url = urlInput.trim();
    const urlParts = url.split('?');

    let path = urlParts[0];
    // ensure path starts with '/'
    path = path.startsWith('/') ? path : `/${path}`;

    const queryParams: { [key: string]: string | boolean } = {};

    // if url has parameters, parse them
    if (urlParts.length > 1) {
      const params = new URLSearchParams(urlParts[1] || '');
      params.forEach((value, key) => {
        queryParams[key] = value;
      });
    }

    return { path, queryParams };
  });

  internalLinkPath = computed(() => this.parsedUrl().path);
  internalLinkQueryParams = computed(() => this.parsedUrl().queryParams);

  isExternalUrl(): boolean {
    const url = this.linkUrl();
    return typeof url === 'string' && isExternalLink(url);
  }
}
