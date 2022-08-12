import { Component } from '@angular/core';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';

@Component({
  selector: 'challenge-registry-challenge-search',
  templateUrl: './challenge-search.component.html',
  styleUrls: ['./challenge-search.component.scss'],
})
export class ChallengeSearchComponent {
  public isPlatformServer = false;

  constructor(private readonly configService: ConfigService) {
    this.isPlatformServer = this.configService.config.isPlatformServer;
  }
}
