import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, Input, OnChanges, SimpleChanges } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'agora-svg-icon',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './svg-icon.component.html',
  styleUrls: ['./svg-icon.component.scss'],
})
export class SvgIconComponent implements OnChanges {
  sanitizer = inject(DomSanitizer);
  http = inject(HttpClient);

  svgContent!: SafeHtml;

  @Input() imagePath = '';
  @Input() altText = '';
  @Input() customClasses = '';

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

  getClasses() {
    const classes = ['svg-icon'];

    if (this.customClasses) {
      classes.push(this.customClasses);
    }

    return classes.join(' ');
  }
}
