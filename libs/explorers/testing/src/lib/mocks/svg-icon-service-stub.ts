import { inject, Injectable } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Observable, of } from 'rxjs';

export const mockSvgTestId = 'mock-svg';

@Injectable({ providedIn: 'root' })
export class SvgIconServiceStub {
  sanitizer = inject(DomSanitizer);

  constructor() {
    // No need to preloadCommonSvgs in the stub
  }

  isValidImagePath(path: string): boolean {
    return true;
  }

  getSvg(path: string): Observable<SafeHtml> {
    return of(
      this.sanitizer.bypassSecurityTrustHtml(`<svg data-testid="${mockSvgTestId}">MockSvg</svg>`),
    );
  }
}
