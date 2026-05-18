import { Injectable, inject } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { isExternalLink } from '@sagebionetworks/shared/util';
import { Observable, of } from 'rxjs';
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
   * Security boundary: only allows SVGs from approved asset directories matching
   * the pattern `<scope>-assets/icons/*.svg` and sanitizes content before rendering.
   */
  private readonly sanitizer = inject(DomSanitizer);

  isValidImagePath(path: string): boolean {
    if (isExternalLink(path)) {
      return false;
    }
    return Boolean(path) && /^[a-z-]+-assets\/icons\/[^/]+\.svg$/.test(path);
  }

  getSvg(path: string): Observable<SafeHtml> {
    if (!this.isValidImagePath(path)) {
      throw new Error('Invalid SVG path');
    }

    const content = SVG_ICON_REGISTRY[path];
    if (content === undefined) {
      console.warn(`SvgIconService: no registry entry for "${path}"`);
      return of('');
    }

    return of(this.sanitizer.bypassSecurityTrustHtml(content));
  }
}
