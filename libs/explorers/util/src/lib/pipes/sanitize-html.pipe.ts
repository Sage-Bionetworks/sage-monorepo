import { inject, Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import sanitizeHtml from 'sanitize-html';

@Pipe({
  name: 'sanitizeHtml',
  standalone: true,
})
export class SanitizeHtmlPipe implements PipeTransform {
  sanitizer = inject(DomSanitizer);

  transform(value: string | null | undefined): SafeHtml {
    if (!value) {
      return '';
    }

    const clean = sanitizeHtml(value);
    return this.sanitizer.bypassSecurityTrustHtml(clean);
  }
}
