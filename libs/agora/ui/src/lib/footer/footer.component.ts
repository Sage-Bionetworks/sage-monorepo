import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Dataversion, DataversionService } from '@sagebionetworks/agora/api-client-angular';
import { Observable } from 'rxjs';
import { SafeUrl } from '@angular/platform-browser';
import { PathSanitizer } from '@sagebionetworks/agora/util';
import { ConfigService } from '@sagebionetworks/agora/config';

@Component({
  selector: 'agora-footer',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit {
  footerLogoPath!: SafeUrl;
  dataVersion$!: Observable<Dataversion>;

  navItems: Array<any> = [
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

  constructor(
    private readonly configService: ConfigService,
    private dataVersionService: DataversionService,
    private sanitizer: PathSanitizer,
  ) {
    this.footerLogoPath = this.sanitizer.sanitize('/agora-assets/images/footer-logo.svg');
  }

  ngOnInit(): void {
    this.dataVersion$ = this.dataVersionService.getDataversion();
  }

  getSiteVersion() {
    return this.configService.config.appVersion;
  }

  getDataVersion(dataVersion: Dataversion) {
    return `${dataVersion.data_file}-${dataVersion.data_version}`;
  }
}
