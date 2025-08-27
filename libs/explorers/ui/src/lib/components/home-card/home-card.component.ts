import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'explorers-home-card',
  imports: [CommonModule, SvgImageComponent, RouterLink, SvgIconComponent],
  templateUrl: './home-card.component.html',
  styleUrls: ['./home-card.component.scss'],
})
export class HomeCardComponent {
  title = input.required<string>();
  description = input.required<string>();

  imagePath = input<string>('/explorers-assets/images/warning-circle.svg');
  imageAltText = input<string>('Warning');
  routerLink = input<string | undefined>();

  secondaryColor = 'inherit';

  constructor() {
    if (typeof document !== 'undefined') {
      this.secondaryColor = getComputedStyle(document.documentElement).getPropertyValue(
        '--color-secondary',
      );
    }
  }
}
