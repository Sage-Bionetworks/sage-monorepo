import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterModule } from '@angular/router';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/constants';
import {
  MetaTagService,
  PlatformService,
  VersionService,
} from '@sagebionetworks/explorers/services';
import {
  ErrorOverlayComponent,
  FooterComponent,
  HeaderComponent,
} from '@sagebionetworks/explorers/ui';
import { DataVersionService } from '@sagebionetworks/model-ad/api-client';
import { ConfigService, MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { SearchInputComponent } from '@sagebionetworks/model-ad/ui';
import { footerLinks, headerLinks } from '@sagebionetworks/model-ad/util';
import {
  CONFIG_SERVICE_TOKEN,
  createGoogleTagManagerIdProvider,
  GoogleTagManagerComponent,
  isGoogleTagManagerIdSet,
} from '@sagebionetworks/shared/google-tag-manager';
import { ToastModule } from 'primeng/toast';

@Component({
  imports: [
    RouterModule,
    ErrorOverlayComponent,
    FooterComponent,
    GoogleTagManagerComponent,
    HeaderComponent,
    SearchInputComponent,
    ToastModule,
  ],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [
    {
      provide: CONFIG_SERVICE_TOKEN,
      useFactory: () => inject(ConfigService),
    },
    {
      provide: LOADING_ICON_COLORS,
      useValue: MODEL_AD_LOADING_ICON_COLORS,
    },
    createGoogleTagManagerIdProvider(),
  ],
})
export class AppComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformService = inject(PlatformService);

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

    this.useGoogleTagManager = isGoogleTagManagerIdSet(
      this.configService.config.googleTagManagerId,
    );
  }

  ngOnInit() {
    if (this.platformService.isBrowser) {
      this.getDataVersion();
      this.getSiteVersion();
    }
  }

  getDataVersion() {
    this.versionService
      .getDataVersion$(this.dataVersionService)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (v) => (this.dataVersion = v),
        error: () => {
          this.dataVersion = 'unknown';
        },
      });
  }

  getSiteVersion() {
    this.versionService
      .getSiteVersion$(this.configService.config)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (v) => (this.siteVersion = v),
        error: () => {
          this.siteVersion = 'unknown';
        },
      });
  }
}
