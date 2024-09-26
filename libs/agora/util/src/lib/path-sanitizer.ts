import { Injectable } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Injectable({
  providedIn: 'root',
})
export class PathSanitizer {
  constructor(private sanitizer: DomSanitizer) {}

  sanitize(path: string) {
    return this.sanitizer.bypassSecurityTrustUrl(path);
  }
}
