import { Component, computed, inject, input, PLATFORM_ID } from '@angular/core';
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

  private parsedUrl = computed(() => {
    const url = this.linkUrl().trim();
    const urlParts = url.split('?');

    let path = urlParts[0];
    // ensure path starts with '/'
    path = path.startsWith('/') ? path : `/${path}`;

    const queryParams: { [key: string]: string | boolean } = {};

    // if url has parameters, parse them
    if (urlParts.length > 1) {
      const paramsString = urlParts[1];
      paramsString.split('&').forEach((param) => {
        const paramParts = param.split('=');
        // Ensure there is a key
        if (paramParts[0]) {
          const key = decodeURIComponent(paramParts[0]);
          const value = paramParts.length > 1 ? decodeURIComponent(paramParts[1]) : true;
          queryParams[key] = value;
        }
      });
    }

    return { path, queryParams };
  });

  isExternalLink(): boolean {
    const url = this.linkUrl();
    return url ? isExternalLink(url) : false;
  }

  internalLinkPath = computed(() => this.parsedUrl().path);
  internalLinkQueryParams = computed(() => this.parsedUrl().queryParams);
}
