import { CommonModule, isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, Input, OnInit, PLATFORM_ID, ViewEncapsulation } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SvgIconService } from '@sagebionetworks/explorers/services';

@Component({
  selector: 'explorers-svg-icon',
  imports: [CommonModule],
  templateUrl: './svg-icon.component.html',
  styleUrls: ['./svg-icon.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SvgIconComponent implements OnInit {
  @Input() imagePath!: string;
  @Input() altText = '';
  @Input() width = 14;
  @Input() height = 14;
  @Input() color = 'inherit'; // Default to parent color
  @Input() enableHoverEffects = true;

  http = inject(HttpClient);
  sanitizer = inject(DomSanitizer);
  svgService = inject(SvgIconService);
  platformId = inject(PLATFORM_ID);

  svgContent: SafeHtml | null = null;

  className = 'svg-icon';

  ngOnInit() {
    if (!this.imagePath) return;

    this.className = this.enableHoverEffects ? 'svg-icon' : 'svg-icon-no-hover';

    // Only load SVG in browser environment to avoid SSR issues
    if (isPlatformBrowser(this.platformId)) {
      this.svgService.getSvg(this.imagePath).subscribe({
        next: (svg) => (this.svgContent = svg),
        error: (error) => console.error('Error loading svg:', error),
      });
    }
  }
}
