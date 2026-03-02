import { Component, computed, input } from '@angular/core';

@Component({
  selector: 'explorers-not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent {
  supportEmail = input('supportEmail'); // from the route data
  backgroundImagePath = input<string | undefined>(); // from the route data

  backgroundImagePathOrDefault = computed(
    () => this.backgroundImagePath() || 'explorers-assets/images/background.svg',
  );
}
