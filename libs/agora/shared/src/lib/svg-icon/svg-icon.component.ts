import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'agora-svg-icon',
  imports: [CommonModule],
  templateUrl: './svg-icon.component.html',
  styleUrls: ['./svg-icon.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SvgIconComponent implements OnInit {
  @Input() imagePath!: string;
  @Input() altText = '';
  @Input() color = 'currentColor'; // Default to inherited color

  http = inject(HttpClient);
  sanitizer = inject(DomSanitizer);

  svgContent: SafeHtml | null = null;

  ngOnInit() {
    if (this.imagePath) {
      this.http.get(this.imagePath, { responseType: 'text' }).subscribe(
        (svgContent) => {
          // Sanitize the SVG content to prevent XSS attacks
          console.log('svg loaded:', svgContent);
          this.svgContent = this.sanitizer.bypassSecurityTrustHtml(svgContent);
          // // Replace the inner HTML of the component with the sanitized SVG content
          // const svgContainer = document.querySelector('.svg-icon-container');
          // if (svgContainer) {
          //   svgContainer.innerHTML = sanitizedSvg as string;
          // }
        },
        (error) => console.error('Error loading svg', error),
      );
    }
  }
}
