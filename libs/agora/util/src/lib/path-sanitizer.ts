import { inject, Injectable } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Injectable({
  providedIn: 'root',
})
export class PathSanitizer {
  private readonly sanitizer = inject(DomSanitizer);

  sanitize(path: string) {
    return this.sanitizer.bypassSecurityTrustUrl(path);
  }
}
