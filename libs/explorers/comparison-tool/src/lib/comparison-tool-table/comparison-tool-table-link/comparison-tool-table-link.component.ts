import { Component, computed, inject, input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HelperService } from '@sagebionetworks/explorers/services';
import { isExternalLink } from '@sagebionetworks/shared/util';

@Component({
  selector: 'explorers-comparison-tool-table-link',
  imports: [RouterModule],
  templateUrl: './comparison-tool-table-link.component.html',
  styleUrls: ['./comparison-tool-table-link.component.scss'],
})
export class ComparisonToolTableLinkComponent {
  private readonly helperService = inject(HelperService);

  linkText = input<string>('');
  linkUrl = input<string>('');

  private readonly parsedUrl = computed(() => {
    const url = this.linkUrl().trim();
    const urlParts = url.split('?');

    let path = urlParts[0];
    // ensure path starts with '/'
    path = path.startsWith('/') ? path : `/${path}`;

    // Encode special characters in the path to handle model names with parentheses and forward slashes
    // e.g., /models/5xFAD (IU/Jax/Pitt) needs proper encoding
    path = this.helperService.encodeParenthesesForwardSlashes(path);

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

  isExternalUrl = computed<boolean>((): boolean => isExternalLink(this.linkUrl()));
}
