import { inject, Injectable } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

export const mockSvgTestId = 'mock-svg';

@Injectable({ providedIn: 'root' })
export class SvgIconServiceStub {
  sanitizer = inject(DomSanitizer);

  isValidImagePath(path: string): boolean {
    return true;
  }

  getSvg(path: string): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(
      `<svg data-testid="${mockSvgTestId}">MockSvg</svg>`,
    );
  }
}
