import { Component } from '@angular/core';
// import { Challenge } from '@challenge-registry/api-angular';

import '@challenge-registry/ui-footer';
// import { FooterComponent } from '@challenge-registry/web/ui';

@Component({
  selector: 'challenge-registry-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  title = 'web-app';
}
