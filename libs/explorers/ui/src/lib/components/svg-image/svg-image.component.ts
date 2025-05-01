import { CommonModule } from '@angular/common';
import { Component, Input, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'explorers-svg-image',
  imports: [CommonModule],
  templateUrl: './svg-image.component.html',
  styleUrls: ['./svg-image.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SvgImageComponent {
  @Input() imagePath = '';
  @Input() altText = '';
}
