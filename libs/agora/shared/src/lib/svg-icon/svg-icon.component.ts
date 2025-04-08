import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, Input, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SvgIconService } from '@sagebionetworks/agora/services';

@Component({
  selector: 'agora-svg-icon',
  imports: [CommonModule],
  templateUrl: './svg-icon.component.html',
  styleUrls: ['./svg-icon.component.scss'],
})
export class SvgIconComponent implements OnInit {
  @Input() imagePath!: string;
  @Input() altText = '';
  @Input() color = 'currentColor'; // Default to inherited color

  http = inject(HttpClient);
  sanitizer = inject(DomSanitizer);
  svgService = inject(SvgIconService);

  svgContent: SafeHtml | null = null;
  private static svgCache = new Map<string, SafeHtml>();

  ngOnInit() {
    if (!this.isValidImagePath(this.imagePath)) return;

    this.svgService.getSvg(this.imagePath).subscribe({
      next: (svg) => (this.svgContent = svg),
      error: (error) => console.error('Error loading svg:', error),
    });
  }

  isValidImagePath(path: string): boolean {
    // We don't want to load SVGs from external sources
    // Ensure the path comes from '/agora-assets/icons/'
    return Boolean(path) && path.startsWith('/agora-assets/icons/');
  }
}
