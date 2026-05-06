import { Component, input } from '@angular/core';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'explorers-resource-card',
  imports: [SvgImageComponent],
  templateUrl: './resource-card.component.html',
  styleUrls: ['./resource-card.component.scss'],
})
export class ResourceCardComponent {
  link = input.required<string>();
  description = input.required<string>();
  imagePath = input.required<string>();
  title = input<string | undefined>();
  altText = input<string>('');
}
