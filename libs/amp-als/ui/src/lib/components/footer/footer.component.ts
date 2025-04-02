import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { ConfigService } from '@sagebionetworks/amp-als/config';
// import { NavigationLink } from '@sagebionetworks/amp-als/models';
// import { GitHubService } from '@sagebionetworks/amp-als/services';
// import { PathSanitizer } from '@sagebionetworks/amp-als/util';
import { Observable } from 'rxjs';

@Component({
  selector: 'amp-als-footer',
  imports: [CommonModule, RouterModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit {
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }
  configService = inject(ConfigService);
  // sanitizer = inject(PathSanitizer);
  // gitHubService = inject(GitHubService);

  footerLogoPath!: SafeUrl;
  sha$!: Observable<string>;

  /*
   TODO find out what the final tag format should be and potentially eliminate the 
   environmental variable for the tag since it can be deterministically generated
   from the site version
   */
  tag = '';

  // navItems: Array<NavigationLink> = [
  //   {
  //     label: 'About',
  //     routerLink: ['about'],
  //   },
  //   {
  //     label: 'Help',
  //     url: 'https://help.adknowledgeportal.org/apd/amp-als-Resources.2646671361.html',
  //     target: '_blank',
  //   },
  //   {
  //     label: 'Terms of Service',
  //     url: 'https://s3.amazonaws.com/static.synapse.org/governance/SageBionetworksSynapseTermsandConditionsofUse.pdf?v=5',
  //     target: '_blank',
  //   },
  // ];

  // constructor() {}

  // ngOnInit(): void {}

  getSiteVersion() {
    // return this.configService.config.appVersion;
  }

  // getDataVersion(dataVersion: Dataversion) {
  //   return `${dataVersion.data_file}-v${dataVersion.data_version}`;
  // }
}
