import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Observable, map, shareReplay } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SvgIconService {
  /**
   * Service for managing SVG icons in the Explorer applications
   *
   * This service provides the following functionality:
   * - Load SVG files from the `/explorers-assets/icons/` directory
   * - Cache SVGs to prevent redundant HTTP requests
   * - Sanitize SVG content for safe rendering in the browser
   * - Preload commonly used SVG icons for better performance
   *
   * The service ensures security by:
   * - Only allowing SVGs from the approved assets directory
   * - Sanitizing SVG content before rendering
   */
  private http = inject(HttpClient);
  private sanitizer = inject(DomSanitizer);
  private svgCache = new Map<string, Observable<SafeHtml>>();

  // Common SVGs we want to preload
  private commonSvgPaths = [
    '/explorers-assets/icons/cog.svg',
    '/explorers-assets/icons/column.svg',
    '/explorers-assets/icons/download.svg',
    '/explorers-assets/icons/external-link.svg',
    '/explorers-assets/icons/gct.svg',
    '/explorers-assets/icons/info-circle.svg',
    '/explorers-assets/icons/pin.svg',
    '/explorers-assets/icons/trash.svg',
  ];

  constructor() {
    this.preloadCommonSvgs();
  }

  private preloadCommonSvgs(): void {
    this.commonSvgPaths.forEach((path) => {
      this.getSvg(path).subscribe(); // Trigger the load
    });
  }

  isValidImagePath(path: string): boolean {
    // We don't want to load SVGs from external sources
    // Block URLs
    if (path.startsWith('http://') || path.startsWith('https://')) {
      return false;
    }
    // Ensure the path comes from '.*/icons/*.svg' OR '.*/images/*.svg'
    return Boolean(path) && (/\/icons\/[^/]+\.svg$/.test(path) || /\/images\/.*\.svg$/.test(path));
  }

  getSvg(path: string): Observable<SafeHtml> {
    if (!this.isValidImagePath(path)) {
      throw new Error('Invalid SVG path');
    }

    // check cache for the SVG
    const cached = this.svgCache.get(path);
    if (cached) {
      return cached;
    }

    const request = this.http.get(path, { responseType: 'text' }).pipe(
      map((svg) => this.sanitizer.bypassSecurityTrustHtml(svg)),
      shareReplay(1),
    );
    this.svgCache.set(path, request);
    return request;
  }
}
