import { Component } from '@angular/core';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';

@Component({
  selector: 'challenge-registry-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
})
export class AboutComponent {
  public appVersion: string;

  constructor(private readonly configService: ConfigService) {
    this.appVersion = this.configService.config.appVersion;
  }
}
