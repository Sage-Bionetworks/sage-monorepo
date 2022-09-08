import { Component } from '@angular/core';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';

@Component({
  selector: 'challenge-registry-team',
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.scss'],
})
export class TeamComponent {
  public appVersion: string;

  constructor(private readonly configService: ConfigService) {
    this.appVersion = this.configService.config.appVersion;
  }
}
