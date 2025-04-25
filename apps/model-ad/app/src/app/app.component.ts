import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConfigService } from '@sagebionetworks/model-ad/config';
import { FooterComponent, HeaderComponent } from '@sagebionetworks/model-ad/ui';
import {
  CONFIG_SERVICE_TOKEN,
  GoogleTagManagerComponent,
  createGoogleTagManagerIdProvider,
  isGoogleTagManagerIdSet,
} from '@sagebionetworks/shared/google-tag-manager';

@Component({
  imports: [RouterModule, HeaderComponent, FooterComponent, GoogleTagManagerComponent],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [
    {
      provide: CONFIG_SERVICE_TOKEN,
      useFactory: () => inject(ConfigService),
    },
    createGoogleTagManagerIdProvider(),
  ],
})
export class AppComponent {
  configService = inject(ConfigService);

  title = 'model-ad-app';
  readonly useGoogleTagManager: boolean;

  constructor() {
    this.useGoogleTagManager = isGoogleTagManagerIdSet(
      this.configService.config.googleTagManagerId,
    );
  }
}
