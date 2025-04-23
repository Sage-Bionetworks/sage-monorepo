import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { Dataversion, DataversionService } from '@sagebionetworks/agora/api-client-angular';
import { ConfigService } from '@sagebionetworks/agora/config';
import { NavigationLink } from '@sagebionetworks/agora/models';
import { GitHubService } from '@sagebionetworks/agora/services';
import { formatAppVersion, PathSanitizer } from '@sagebionetworks/agora/util';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'agora-footer',
  imports: [CommonModule, RouterModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  configService = inject(ConfigService);
  dataVersionService = inject(DataversionService);
  sanitizer = inject(PathSanitizer);
  gitHubService = inject(GitHubService);

  footerLogoPath!: SafeUrl;
  siteVersion = '';
  dataVersion = '';

  navItems: Array<NavigationLink> = [
    {
      label: 'About',
      routerLink: ['about'],
    },
    {
      label: 'Help',
      url: 'https://help.adknowledgeportal.org/apd/Agora-Resources.2646671361.html',
      target: '_blank',
    },
    {
      label: 'Terms of Service',
      url: 'https://s3.amazonaws.com/static.synapse.org/governance/SageBionetworksSynapseTermsandConditionsofUse.pdf?v=5',
      target: '_blank',
    },
  ];

  constructor() {
    this.footerLogoPath = this.sanitizer.sanitize('/agora-assets/images/footer-logo.svg');

    const tag = this.configService.config.tagName;
    this.gitHubService
      .getCommitSHA(tag)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (sha) => {
          this.siteVersion = this.getSiteVersion(sha);
        },
        error: (error) => console.error('Error loading commit SHA:', error),
      });
    this.dataVersionService
      .getDataversion()
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (data) => {
          this.dataVersion = this.getDataVersion(data);
        },
        error: (error) => console.error('Error loading data version:', error),
      });
  }

  getSiteVersion(sha: string) {
    const appVersion = formatAppVersion(this.configService.config.appVersion);
    return sha ? `${appVersion}-${sha}` : `${appVersion}`;
  }

  getDataVersion(dataVersion: Dataversion) {
    return `${dataVersion.data_file}-v${dataVersion.data_version}`;
  }
}
