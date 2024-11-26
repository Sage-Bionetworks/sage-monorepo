import { CommonModule } from '@angular/common';
import { Component, Input, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'agora-svg-image',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './svg-image.component.html',
  styleUrls: ['./svg-image.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SvgImageComponent {
  @Input() imagePath = '';
  @Input() altText = '';
}
