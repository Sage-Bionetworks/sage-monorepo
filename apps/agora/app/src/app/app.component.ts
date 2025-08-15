import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConfigService } from '@sagebionetworks/agora/config';
import { MetaTagService } from '@sagebionetworks/agora/services';
import { HeaderComponent } from '@sagebionetworks/agora/ui';
import { FooterComponent } from '@sagebionetworks/explorers/ui';
import { footerLinks } from '@sagebionetworks/agora/util';
import {
  CONFIG_SERVICE_TOKEN,
  GoogleTagManagerComponent,
  createGoogleTagManagerIdProvider,
  isGoogleTagManagerIdSet,
} from '@sagebionetworks/shared/google-tag-manager';
import { ToastModule } from 'primeng/toast';

@Component({
  imports: [RouterModule, HeaderComponent, FooterComponent, ToastModule, GoogleTagManagerComponent],
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
  metaTagService = inject(MetaTagService);
  configService = inject(ConfigService);

  readonly useGoogleTagManager: boolean;

  dataVersion = '';

  footerLinks = footerLinks;

  constructor() {
    this.metaTagService.updateMetaTags();
    this.useGoogleTagManager = isGoogleTagManagerIdSet(
      this.configService.config.googleTagManagerId,
    );
  }
}
