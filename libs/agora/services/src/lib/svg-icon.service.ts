import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Observable, map, shareReplay } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SvgIconService {
  private http = inject(HttpClient);
  private sanitizer = inject(DomSanitizer);
  private svgCache = new Map<string, Observable<SafeHtml>>();

  // Common SVGs we want to preload
  private commonSvgPaths = [
    '/agora-assets/icons/cog.svg',
    '/agora-assets/icons/column.svg',
    '/agora-assets/icons/download.svg',
    '/agora-assets/icons/external-link.svg',
    '/agora-assets/icons/gct.svg',
    '/agora-assets/icons/info-circle.svg',
    '/agora-assets/icons/pin.svg',
    '/agora-assets/icons/trash.svg',
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
    // Ensure the path comes from '/agora-assets/icons/'
    return Boolean(path) && path.startsWith('/agora-assets/icons/');
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
