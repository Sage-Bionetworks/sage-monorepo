import { Component, input } from '@angular/core';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'explorers-stat-card',
  imports: [SvgImageComponent],
  templateUrl: './stat-card.component.html',
  styleUrls: ['./stat-card.component.scss'],
})
export class StatCardComponent {
  iconPath = input.required<string>();
  header = input.required<string>();
  subHeader = input<string | undefined>();
  iconAltText = input<string>('');
}
