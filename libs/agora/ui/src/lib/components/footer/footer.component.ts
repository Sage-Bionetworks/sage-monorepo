import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { Dataversion, DataversionService } from '@sagebionetworks/agora/api-client-angular';
import { ConfigService } from '@sagebionetworks/agora/config';
import { NavigationLink } from '@sagebionetworks/agora/models';
import { GitHubService } from '@sagebionetworks/agora/services';
import { formatGitTag, PathSanitizer } from '@sagebionetworks/agora/util';
import { Observable } from 'rxjs';

@Component({
  selector: 'agora-footer',
  imports: [CommonModule, RouterModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit {
  configService = inject(ConfigService);
  dataVersionService = inject(DataversionService);
  sanitizer = inject(PathSanitizer);
  gitHubService = inject(GitHubService);

  footerLogoPath!: SafeUrl;
  dataVersion$!: Observable<Dataversion>;
  sha = '';

  /*
   TODO find out what the final tag format should be and potentially eliminate the 
   environmental variable for the tag since it can be deterministically generated
   from the site version
   */
  tag = '';

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
    this.tag = this.configService.config.tagName;
  }

  ngOnInit(): void {
    this.dataVersion$ = this.dataVersionService.getDataversion();
    this.gitHubService.getCommitSHA(this.tag).subscribe((sha) => {
      this.sha = sha;
    });
  }

  getSiteVersion() {
    const appVersion = formatGitTag(this.configService.config.appVersion);
    return this.sha ? `${appVersion}-${this.sha}` : `${appVersion}`;
  }

  getDataVersion(dataVersion: Dataversion) {
    return `${dataVersion.data_file}-v${dataVersion.data_version}`;
  }
}
