import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterModule } from '@angular/router';
import {
  GitHubService,
  MetaTagService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import { HeaderComponent } from '@sagebionetworks/agora/ui';
import { FooterComponent } from '@sagebionetworks/explorers/ui';
import { DataversionService, Dataversion } from '@sagebionetworks/agora/api-client-angular';
import { ConfigService } from '@sagebionetworks/agora/config';
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
export class AppComponent implements OnInit {
  private platformService = inject(PlatformService);
  private destroyRef = inject(DestroyRef);

  configService = inject(ConfigService);
  dataVersionService = inject(DataversionService);
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
        .getDataversion()
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (data) => {
            this.dataVersion = this.formatDataVersion(data);
          },
          error: (error) => console.error('Error loading data version:', error),
        });
    }
  }

  formatDataVersion(dataVersion: Dataversion) {
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
