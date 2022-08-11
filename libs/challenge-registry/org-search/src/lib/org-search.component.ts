import { Component } from '@angular/core';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';

@Component({
  selector: 'challenge-registry-org-search',
  templateUrl: './org-search.component.html',
  styleUrls: ['./org-search.component.scss'],
})
export class OrgSearchComponent {
  public appVersion: string;

  constructor(private readonly configService: ConfigService) {
    this.appVersion = this.configService.config.appVersion;
  }
}
