import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/constants';
import {
  MetaTagService,
  PlatformService,
  VersionService,
} from '@sagebionetworks/explorers/services';
import { FooterComponent, HeaderComponent } from '@sagebionetworks/explorers/ui';
import { DataVersionService } from '@sagebionetworks/model-ad/api-client';
import { ConfigService, MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { SearchInputComponent } from '@sagebionetworks/model-ad/ui';
import { footerLinks, headerLinks } from '@sagebionetworks/model-ad/util';
import {
  GoogleTagManagerComponent,
  isGtmIdSet,
  GTM_CONFIG,
} from '@sagebionetworks/web-shared/angular/analytics/gtm';
import { ToastModule } from 'primeng/toast';

@Component({
  imports: [
    RouterModule,
    FooterComponent,
    HeaderComponent,
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
      useValue: MODEL_AD_LOADING_ICON_COLORS,
    },
  ],
})
export class AppComponent implements OnInit {
  private readonly platformService = inject(PlatformService);
  private readonly destroyRef = inject(DestroyRef);

  configService = inject(ConfigService);
  dataVersionService = inject(DataVersionService);
  metaTagService = inject(MetaTagService);
  versionService = inject(VersionService);

  readonly useGoogleTagManager: boolean;

  dataVersion = '';
  siteVersion = '';

  headerLinks = headerLinks;
  footerLinks = footerLinks;

  constructor() {
    this.metaTagService.initialize('Model AD');

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
