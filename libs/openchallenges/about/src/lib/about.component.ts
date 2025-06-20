
import { Component, Renderer2 } from '@angular/core';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { FooterComponent } from '@sagebionetworks/openchallenges/ui';
import { getSeoData } from './about-seo-data';
import { SeoService } from '@sagebionetworks/shared/util';

@Component({
  selector: 'openchallenges-about',
  imports: [FooterComponent],
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
})
export class AboutComponent {
  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;

  constructor(
    private readonly configService: ConfigService,
    private seoService: SeoService,
    private renderer2: Renderer2,
  ) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
    this.privacyPolicyUrl = this.configService.config.privacyPolicyUrl;
    this.termsOfUseUrl = this.configService.config.termsOfUseUrl;
    this.apiDocsUrl = this.configService.config.apiDocsUrl;
    this.seoService.setData(getSeoData(), this.renderer2);
  }
}
