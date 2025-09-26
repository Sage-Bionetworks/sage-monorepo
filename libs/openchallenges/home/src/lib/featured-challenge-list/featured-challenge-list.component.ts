import { AsyncPipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import {
  Challenge,
  ChallengeService,
  ChallengeSearchQuery,
  ChallengeCategory,
  ChallengeSort,
} from '@sagebionetworks/openchallenges/api-client';
import { ChallengeCardComponent } from '@sagebionetworks/openchallenges/ui';
import { Observable, catchError, map, of, switchMap, throwError } from 'rxjs';

@Component({
  selector: 'openchallenges-featured-challenge-list',
  imports: [AsyncPipe, ChallengeCardComponent],
  templateUrl: './featured-challenge-list.component.html',
  styleUrls: ['./featured-challenge-list.component.scss'],
})
export class FeaturedChallengeListComponent implements OnInit {
  private readonly challengeService = inject(ChallengeService);

  challenges$!: Observable<Challenge[]>;

  ngOnInit() {
    const defaultQuery: ChallengeSearchQuery = {
      pageNumber: 0,
      pageSize: 3, // only display first 3 for now
      searchTerms: '',
      sort: ChallengeSort.StartDate,
    };

    const query: ChallengeSearchQuery = {
      ...defaultQuery,
      categories: [ChallengeCategory.Featured],
    };

    const challengesPage$ = this.challengeService.listChallenges(query).pipe(
      catchError((err) => {
        return throwError(() => new Error(err.message));
      }),
    );
    this.challenges$ = challengesPage$.pipe(
      switchMap((page) =>
        // remove categories filter if no featured challenges
        page.challenges ? of(page) : this.challengeService.listChallenges(defaultQuery),
      ),
      map((page) => page.challenges),
    );
  }
}
