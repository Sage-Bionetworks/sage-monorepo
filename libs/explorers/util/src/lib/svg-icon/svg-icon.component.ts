import { CommonModule } from '@angular/common';
import { Component, computed, inject, input, OnInit, ViewEncapsulation } from '@angular/core';
import { SafeHtml } from '@angular/platform-browser';
import { SvgIconService } from '@sagebionetworks/explorers/services';

export type SvgIconBackgroundShape = 'circle' | 'square';

@Component({
  selector: 'explorers-svg-icon',
  imports: [CommonModule],
  templateUrl: './svg-icon.component.html',
  styleUrls: ['./svg-icon.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SvgIconComponent implements OnInit {
  imagePath = input.required<string>();
  altText = input('');
  width = input(14);
  height = input(14);
  color = input('inherit');
  enableHoverEffects = input(false);

  enableBackground = input(false);
  backgroundColor = input('var(--color-primary)');
  backgroundShape = input<SvgIconBackgroundShape>('circle');
  backgroundPadding = input(8);

  private readonly svgService = inject(SvgIconService);

  svgContent: SafeHtml | null = null;

  className = computed(() => (this.enableHoverEffects() ? 'svg-icon' : 'svg-icon-no-hover'));

  ngOnInit() {
    this.svgService.getSvg(this.imagePath()).subscribe({
      next: (svg) => (this.svgContent = svg),
      error: () => {
        // Handled by httpErrorInterceptor
      },
    });
  }
}
