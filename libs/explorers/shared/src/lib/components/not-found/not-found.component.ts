import { Component, computed, input } from '@angular/core';

@Component({
  selector: 'explorers-not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent {
  supportEmail = input('supportEmail'); // from the route data
  heroBackgroundImagePath = input<string | undefined>(); // from the route data

  heroBackgroundImagePathOrDefault = computed(
    () => this.heroBackgroundImagePath() ?? 'explorers-assets/images/background.svg',
  );
}
