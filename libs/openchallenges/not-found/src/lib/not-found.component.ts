import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { ConfigService } from '@sagebionetworks/openchallenges/web/angular/config';
import { FooterComponent } from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-not-found',
  imports: [RouterModule, MatCardModule, MatButtonModule, FooterComponent],
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss'],
})
export class NotFoundComponent {
  private readonly configService = inject(ConfigService);

  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;

  constructor() {
    this.appVersion = this.configService.config.app.version;
    this.dataUpdatedOn = this.configService.config.data.updatedOn;
    this.privacyPolicyUrl = this.configService.config.links.privacyPolicy;
    this.termsOfUseUrl = this.configService.config.links.termsOfUse;
    this.apiDocsUrl = this.configService.config.api.docs.url;
  }
}
