import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import {
  Component,
  computed,
  effect,
  inject,
  input,
  signal,
  ViewEncapsulation,
} from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SvgIconBackgroundShape } from '@sagebionetworks/explorers/models';
import { SvgIconService } from '@sagebionetworks/explorers/services';

@Component({
  selector: 'explorers-svg-icon',
  imports: [CommonModule],
  templateUrl: './svg-icon.component.html',
  styleUrls: ['./svg-icon.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SvgIconComponent {
  imagePath = input.required<string>();
  altText = input('');
  width = input(14);
  height = input(14);
  color = input('inherit');
  enableHoverEffects = input(true);
  backgroundColor = input<string | undefined>();
  backgroundShape = input<SvgIconBackgroundShape>('circle');
  backgroundPadding = input(8);

  http = inject(HttpClient);
  sanitizer = inject(DomSanitizer);
  svgService = inject(SvgIconService);

  svgContent = signal<SafeHtml | null>(null);

  className = computed(() => (this.enableHoverEffects() ? 'svg-icon' : 'svg-icon-no-hover'));

  constructor() {
    effect(() => {
      const path = this.imagePath();
      if (!path) return;
      this.svgService.getSvg(path).subscribe({
        next: (svg) => this.svgContent.set(svg),
        error: () => {
          // Handled by httpErrorInterceptor
        },
      });
    });
  }
}
