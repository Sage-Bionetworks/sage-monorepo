import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { NavComponent, FooterComponent } from '@sagebionetworks/bixarena/ui';
import { GtmComponent } from '@sagebionetworks/web-shared/angular/analytics/gtm';

@Component({
  imports: [RouterModule, NavComponent, FooterComponent, GtmComponent],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  readonly useGtm = inject(ConfigService).config.analytics.googleTagManager.enabled;
}
