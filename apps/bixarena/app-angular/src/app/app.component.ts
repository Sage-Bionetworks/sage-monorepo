import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { BattleGateService } from '@sagebionetworks/bixarena/services';
import { NavComponent, FooterComponent, LoginModalComponent } from '@sagebionetworks/bixarena/ui';
import { GtmComponent } from '@sagebionetworks/web-shared/angular/analytics/gtm';

@Component({
  imports: [RouterModule, NavComponent, FooterComponent, LoginModalComponent, GtmComponent],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  readonly useGoogleTagManager = inject(ConfigService).config.analytics.googleTagManager.enabled;
  readonly gate = inject(BattleGateService);
}
