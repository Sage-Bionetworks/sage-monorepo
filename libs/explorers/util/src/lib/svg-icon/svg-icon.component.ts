import { CommonModule } from '@angular/common';
import { Component, computed, inject, input, ViewEncapsulation } from '@angular/core';
import { SvgIconService } from '@sagebionetworks/explorers/services';

export type SvgIconBackgroundShape = 'circle' | 'square';

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
  strokeWidth = input<number>();
  enableHoverEffects = input(false);

  enableBackground = input(false);
  backgroundColor = input('var(--color-primary)');
  backgroundShape = input<SvgIconBackgroundShape>('circle');
  backgroundPadding = input(8);

  private readonly svgService = inject(SvgIconService);

  readonly svgContent = computed(() => this.svgService.getSvg(this.imagePath()));
  readonly className = computed(() =>
    this.enableHoverEffects() ? 'svg-icon' : 'svg-icon-no-hover',
  );
}
