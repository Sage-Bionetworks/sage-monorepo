import { Component, input } from '@angular/core';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'explorers-stat-card',
  imports: [SvgImageComponent],
  templateUrl: './stat-card.component.html',
  styleUrls: ['./stat-card.component.scss'],
})
export class StatCardComponent {
  value = input.required<string>();
  label = input.required<string>();
  iconPath = input<string | undefined>();
  iconAltText = input<string>('');
}
