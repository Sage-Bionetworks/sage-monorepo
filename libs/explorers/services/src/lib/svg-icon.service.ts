import { Injectable, inject } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { isExternalLink } from '@sagebionetworks/shared/util';
import { SVG_ICON_REGISTRY } from './svg-icon-registry.gen';

@Injectable({
  providedIn: 'root',
})
export class SvgIconService {
  /**
   * Service for resolving SVG icons in the Explorer applications.
   *
   * SVG content is bundled at build time via the registry in
   * `svg-icon-registry.gen.ts` (regenerate with `pnpm generate:svg-icon-registry`).
   * No HTTP request is made — content is available synchronously on both server
   * and client, so SSR-rendered HTML contains the inline SVG markup directly.
   *
   * Security boundary: only allows SVGs from the `explorers-assets/icons/` directory
   * and sanitizes content before rendering.
   */
  private readonly sanitizer = inject(DomSanitizer);

  isValidImagePath(path: string): boolean {
    if (isExternalLink(path)) {
      return false;
    }
    return Boolean(path) && /^explorers-assets\/icons\/[^/]+\.svg$/.test(path);
  }

  getSvg(path: string): SafeHtml {
    if (!this.isValidImagePath(path)) {
      throw new Error('Invalid SVG path');
    }

    const content = SVG_ICON_REGISTRY[path];
    if (content === undefined) {
      console.warn(`SvgIconService: no registry entry for "${path}"`);
      return this.sanitizer.bypassSecurityTrustHtml('');
    }

    return this.sanitizer.bypassSecurityTrustHtml(content);
  }
}
