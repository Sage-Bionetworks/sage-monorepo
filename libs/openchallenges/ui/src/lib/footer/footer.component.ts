import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfigService } from '@sagebionetworks/openchallenges/config';

@Component({
  selector: 'openchallenges-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  @Input() version = 'x.y.z';
  privacyPolicyUrl: string;
  termsOfUseUrl: string;

  constructor(private readonly configService: ConfigService) {
    this.privacyPolicyUrl = this.configService.config.privacyPolicyUrl;
    this.termsOfUseUrl = this.configService.config.termsOfUseUrl;
  }
}
