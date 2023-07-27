import { Component, OnInit } from '@angular/core';
import {
  Challenge,
  ChallengeService,
  ChallengeSearchQuery,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable, catchError, map, throwError } from 'rxjs';

@Component({
  selector: 'openchallenges-featured-challenge-list',
  templateUrl: './featured-challenge-list.component.html',
  styleUrls: ['./featured-challenge-list.component.scss'],
})
export class FeaturedChallengeListComponent implements OnInit {
  challenges$!: Observable<Challenge[]>;

  constructor(private challengeService: ChallengeService) {}

  ngOnInit() {
    const query: ChallengeSearchQuery = {
      pageNumber: 0,
      pageSize: 4, // only display first 4 for now
      categories: ['featured'],
      searchTerms: '',
      sort: 'recently_started',
    };
    this.challenges$ = this.challengeService.listChallenges(query).pipe(
      map((page) => page.challenges),
      catchError((err) => {
        return throwError(() => new Error(err.message));
      })
    );
  }
}
