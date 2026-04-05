import { Component, inject } from '@angular/core';
import { ConfigService } from '@sagebionetworks/bixarena/config';

@Component({
  selector: 'bixarena-footer',
  imports: [],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss',
})
export class FooterComponent {
  private readonly config = inject(ConfigService).config;
  protected readonly version = this.config.app.version;
  protected readonly termsOfServiceUrl = this.config.app.termsOfServiceUrl;
  protected readonly contactUrl = this.config.app.contactUrl;
  protected readonly feedbackUrl = this.config.app.feedbackUrl;
  protected readonly sageBionetworksUrl = this.config.app.sageBionetworksUrl;
}
