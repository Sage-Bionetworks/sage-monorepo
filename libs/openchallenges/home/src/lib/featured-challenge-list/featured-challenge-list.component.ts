import { Component, OnInit } from '@angular/core';
import {
  Challenge,
  ChallengeService,
  ChallengeSearchQuery,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable, catchError, map, of, switchMap, throwError } from 'rxjs';

@Component({
  selector: 'openchallenges-featured-challenge-list',
  templateUrl: './featured-challenge-list.component.html',
  styleUrls: ['./featured-challenge-list.component.scss'],
})
export class FeaturedChallengeListComponent implements OnInit {
  challenges$!: Observable<Challenge[]>;

  constructor(private challengeService: ChallengeService) {}

  ngOnInit() {
    const defaultQuery: ChallengeSearchQuery = {
      pageNumber: 0,
      pageSize: 3, // only display first 3 for now
      searchTerms: '',
      sort: 'recently_started',
    };

    const query: ChallengeSearchQuery = {
      ...defaultQuery,
      categories: ['featured'],
    };

    const challengesPage$ = this.challengeService.listChallenges(query).pipe(
      catchError((err) => {
        return throwError(() => new Error(err.message));
      })
    );
    this.challenges$ = challengesPage$.pipe(
      switchMap((page) =>
        // remove categories filter if no featured challenges
        page.challenges
          ? of(page)
          : this.challengeService.listChallenges(defaultQuery)
      ),
      map((page) => page.challenges)
    );
  }
}
