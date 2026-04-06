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
  protected readonly termsOfServiceUrl = this.config.links.termsOfService;
  protected readonly contactUrl = this.config.links.contact;
  protected readonly feedbackUrl = this.config.links.feedback;
  protected readonly sageBionetworksUrl = this.config.links.sageBionetworks;
}
