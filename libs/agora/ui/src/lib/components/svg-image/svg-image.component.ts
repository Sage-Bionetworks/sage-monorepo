import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, Input, OnChanges, SimpleChanges } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'agora-svg-image',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './svg-image.component.html',
  styleUrls: ['./svg-image.component.scss'],
})
export class SvgImageComponent implements OnChanges {
  sanitizer = inject(DomSanitizer);
  http = inject(HttpClient);

  svgContent!: SafeHtml;

  @Input() imagePath = '';
  @Input() altText = '';

  ngOnChanges(changes: SimpleChanges) {
    if (changes['imagePath']) {
      this.http.get(this.imagePath, { responseType: 'text' }).subscribe(
        (svg: string) => {
          this.svgContent = this.sanitizer.bypassSecurityTrustHtml(svg);
        },
        (error) => {
          console.error('Error loading SVG file:', error);
        },
      );
    }
  }
}
