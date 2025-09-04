import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterModule } from '@angular/router';
import { DataVersion, DataVersionService } from '@sagebionetworks/agora/api-client-angular';
import { AGORA_LOADING_ICON_COLORS, ConfigService } from '@sagebionetworks/agora/config';
import { HeaderComponent } from '@sagebionetworks/agora/ui';
import { footerLinks } from '@sagebionetworks/agora/util';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/constants';
import {
  GitHubService,
  MetaTagService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import { FooterComponent } from '@sagebionetworks/explorers/ui';
import {
  CONFIG_SERVICE_TOKEN,
  createGoogleTagManagerIdProvider,
  GoogleTagManagerComponent,
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
    {
      provide: LOADING_ICON_COLORS,
      useValue: AGORA_LOADING_ICON_COLORS,
    },
    createGoogleTagManagerIdProvider(),
  ],
})
export class AppComponent implements OnInit {
  private platformService = inject(PlatformService);
  private destroyRef = inject(DestroyRef);

  configService = inject(ConfigService);
  dataVersionService = inject(DataVersionService);
  gitHubService = inject(GitHubService);
  metaTagService = inject(MetaTagService);

  readonly useGoogleTagManager: boolean;

  siteVersion = '';
  dataVersion = '';

  footerLinks = footerLinks;

  constructor() {
    this.metaTagService.initialize('Agora');

    this.useGoogleTagManager = isGoogleTagManagerIdSet(
      this.configService.config.googleTagManagerId,
    );
  }

  ngOnInit() {
    this.getDataVersion();
    this.getSiteVersion();
  }

  getDataVersion() {
    if (this.platformService.isBrowser) {
      this.dataVersionService
        .getDataVersion()
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (data) => {
            this.dataVersion = this.formatDataVersion(data);
          },
          error: (error) => console.error('Error loading data version:', error),
        });
    }
  }

  formatDataVersion(dataVersion: DataVersion) {
    return `${dataVersion.data_file}-v${dataVersion.data_version}`;
  }

  getSiteVersion() {
    if (this.platformService.isBrowser) {
      const tag = this.configService.config.tagName;
      this.gitHubService
        .getCommitSHA(tag)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (sha) => {
            this.siteVersion = this.formatSiteVersion(sha);
          },
          error: (error) => console.error('Error loading commit SHA:', error),
        });
    }
  }

  formatSiteVersion(sha: string) {
    const appVersion = this.formatAppVersion(this.configService.config.appVersion);
    return sha ? `${appVersion}-${sha}` : `${appVersion}`;
  }

  formatAppVersion(appVersion: string) {
    // remove the -rcX suffix
    return appVersion.replace(/-rc\d+$/, '');
  }
}
