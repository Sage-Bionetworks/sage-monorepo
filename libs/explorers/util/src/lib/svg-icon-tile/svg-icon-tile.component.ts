import { Component, input, ViewEncapsulation } from '@angular/core';
import { SvgIconComponent } from '../svg-icon/svg-icon.component';

export type SvgIconTileShape = 'circle' | 'square';

@Component({
  selector: 'explorers-svg-icon-tile',
  imports: [SvgIconComponent],
  templateUrl: './svg-icon-tile.component.html',
  styleUrls: ['./svg-icon-tile.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SvgIconTileComponent {
  imagePath = input.required<string>();
  altText = input('');
  width = input(14);
  height = input(14);
  color = input('inherit');

  backgroundColor = input.required<string>();
  shape = input<SvgIconTileShape>('circle');
  padding = input(8);
}
