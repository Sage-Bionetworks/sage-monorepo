import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { ChallengeSearchDataService } from './challenge-search-data.service';

@Component({
  selector: 'openchallenges-challenge-search',
  templateUrl: './challenge-search.component.html',
  styleUrls: ['./challenge-search.component.scss'],
})
export class ChallengeSearchComponent {
  public isPlatformServer = false;
  searchTerm!: string | undefined;

  constructor(
    private readonly configService: ConfigService,
    private router: Router,
    private challengeSearchDataService: ChallengeSearchDataService
  ) {
    this.isPlatformServer = this.configService.config.isPlatformServer;
  }

  onClick(): void {
    this.challengeSearchDataService.setSearchTerm(this.searchTerm);
    this.router.navigate(['/challenges']);
  }
}
