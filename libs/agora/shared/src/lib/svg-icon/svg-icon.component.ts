import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { firstValueFrom } from 'rxjs';

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
    if (!this.isValidImagePath(this.imagePath)) return;
    this.loadSVG();
  }

  isValidImagePath(path: string) {
    // We don't want to load SVGs from external sources
    // Ensure the path comes from '/agora-assets/'
    if (path && path.startsWith('/agora-assets/')) {
      return true;
    }
    return false;
  }

  async loadSVG() {
    try {
      const svgContent = await firstValueFrom(
        this.http.get(this.imagePath, { responseType: 'text' }),
      );
      this.svgContent = this.sanitizer.bypassSecurityTrustHtml(svgContent);
    } catch (error) {
      console.error('Error loading svg', error);
    }
  }
}
