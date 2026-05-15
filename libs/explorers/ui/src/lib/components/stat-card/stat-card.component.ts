import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-stat-card',
  imports: [RouterLink, SvgIconComponent],
  templateUrl: './stat-card.component.html',
  styleUrls: ['./stat-card.component.scss'],
})
export class StatCardComponent {
  iconPath = input.required<string>();
  iconAltText = input.required<string>();
  header = input.required<string>();
  link = input.required<string>();
  subHeader = input<string | undefined>();
  iconBackgroundColor = input<string | undefined>();
  iconColor = input<string | undefined>();
}
