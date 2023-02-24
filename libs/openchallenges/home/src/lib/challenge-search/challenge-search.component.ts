import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ConfigService } from '@sagebionetworks/openchallenges/config';

@Component({
  selector: 'openchallenges-challenge-search',
  templateUrl: './challenge-search.component.html',
  styleUrls: ['./challenge-search.component.scss'],
})
export class ChallengeSearchComponent {
  public isPlatformServer = false;
  searchTerms!: string | undefined;

  constructor(
    private readonly configService: ConfigService,
    private router: Router
  ) {
    this.isPlatformServer = this.configService.config.isPlatformServer;
  }

  onSearch(): void {
    this.router.navigateByUrl('/challenges?searchTerms=' + this.searchTerms);
  }
}
