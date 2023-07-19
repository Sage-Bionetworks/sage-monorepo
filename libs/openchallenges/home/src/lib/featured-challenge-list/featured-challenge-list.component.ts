import { Component, OnInit } from '@angular/core';
import {
  Challenge,
  ChallengeService,
  ChallengeSearchQuery,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { BehaviorSubject, switchMap } from 'rxjs';

@Component({
  selector: 'openchallenges-featured-challenge-list',
  templateUrl: './featured-challenge-list.component.html',
  styleUrls: ['./featured-challenge-list.component.scss'],
})
export class FeaturedChallengeListComponent implements OnInit {
  private query: BehaviorSubject<ChallengeSearchQuery> =
    new BehaviorSubject<ChallengeSearchQuery>({});

  challenges: Challenge[] = [];

  constructor(private challengeService: ChallengeService) {}

  ngOnInit() {
    const defaultQuery: ChallengeSearchQuery = {
      pageNumber: 0,
      pageSize: 3,
      searchTerms: '',
      sort: 'recently_started',
    };
    this.query.next(defaultQuery);
    this.query
      // TODO: update to retrieve featured challenges
      .pipe(switchMap((query) => this.challengeService.listChallenges(query)))
      .subscribe((page) => {
        this.challenges = page.challenges;
      });
  }
}
