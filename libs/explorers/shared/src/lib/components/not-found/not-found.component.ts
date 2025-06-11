import { Component, input } from '@angular/core';

@Component({
  selector: 'explorers-not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent {
  supportEmail = input('supportEmail'); // from the route data
  readonly backgroundImagePath = 'explorers-assets/images/background.svg';
}
