
import { Component, HostBinding, Input, ViewEncapsulation } from '@angular/core';

type SvgSizeMode = 'full-height' | 'full-width' | 'original';

@Component({
  selector: 'explorers-svg-image',
  imports: [],
  templateUrl: './svg-image.component.html',
  styleUrls: ['./svg-image.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SvgImageComponent {
  @Input() imagePath = '';
  @Input() altText = '';
  @Input() sizeMode: SvgSizeMode = 'original';

  @HostBinding('class') get hostClasses() {
    return `svg-image-container svg-image-container--${this.sizeMode}`;
  }
}
