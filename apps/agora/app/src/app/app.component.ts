import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DataVersionService } from '@sagebionetworks/agora/api-client';
import { AGORA_LOADING_ICON_COLORS, ConfigService } from '@sagebionetworks/agora/config';
import { SearchInputComponent } from '@sagebionetworks/agora/ui';
import { footerLinks, headerLinks } from '@sagebionetworks/agora/util';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/constants';
import {
  GitHubService,
  MetaTagService,
  PlatformService,
  VersionService,
} from '@sagebionetworks/explorers/services';
import { FooterComponent, HeaderComponent } from '@sagebionetworks/explorers/ui';
import {
  GoogleTagManagerComponent,
  isGtmIdSet,
  GTM_CONFIG,
} from '@sagebionetworks/web-shared/angular/analytics/gtm';
import { ToastModule } from 'primeng/toast';

@Component({
  imports: [
    RouterModule,
    HeaderComponent,
    FooterComponent,
    ToastModule,
    GoogleTagManagerComponent,
    SearchInputComponent,
  ],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [
    {
      provide: GTM_CONFIG,
      useFactory: () => {
        const config = inject(ConfigService);
        return {
          gtmId: config.config.googleTagManagerId,
          isPlatformServer: config.config.isPlatformServer,
        };
      },
    },
    {
      provide: LOADING_ICON_COLORS,
      useValue: AGORA_LOADING_ICON_COLORS,
    },
  ],
})
export class AppComponent implements OnInit {
  private readonly platformService = inject(PlatformService);
  private readonly destroyRef = inject(DestroyRef);

  configService = inject(ConfigService);
  dataVersionService = inject(DataVersionService);
  gitHubService = inject(GitHubService);
  metaTagService = inject(MetaTagService);
  versionService = inject(VersionService);

  readonly useGoogleTagManager: boolean;

  siteVersion = '';
  dataVersion = '';

  headerLinks = headerLinks;
  footerLinks = footerLinks;

  constructor() {
    this.metaTagService.initialize('Agora');

    this.useGoogleTagManager = isGtmIdSet(this.configService.config.googleTagManagerId);
  }

  ngOnInit() {
    this.getDataVersion();
    this.getSiteVersion();
  }

  getDataVersion() {
    this.versionService.getDataVersion(this.dataVersionService, (dataVersion) => {
      this.dataVersion = dataVersion;
    });
  }

  getSiteVersion() {
    this.versionService.getSiteVersion(this.configService.config, (siteVersion) => {
      this.siteVersion = siteVersion;
    });
  }
}
