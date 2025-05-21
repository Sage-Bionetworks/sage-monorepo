import { CommonModule } from '@angular/common';
import { Component, Renderer2 } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { RouterModule } from '@angular/router';
import { ConfigService } from '@sagebionetworks/model-ad/config';
import { SeoService } from '@sagebionetworks/shared/util';
import { getSeoData } from './seo-data';

@Component({
  selector: 'model-ad-not-found',
  imports: [CommonModule, RouterModule, MatCardModule, MatButtonModule],
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent {
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
