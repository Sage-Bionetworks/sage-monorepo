import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  Challenge,
  ChallengeService,
  ChallengeSearchQuery,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ChallengeCardComponent } from '@sagebionetworks/openchallenges/ui';
import { Observable, catchError, map, of, switchMap, throwError } from 'rxjs';

@Component({
  selector: 'openchallenges-random-challenge-list',
  standalone: true,
  imports: [CommonModule, ChallengeCardComponent],
  templateUrl: './random-challenge-list.component.html',
  styleUrls: ['./random-challenge-list.component.scss'],
})
export class RandomChallengeListComponent implements OnInit {
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
        // remove categories filter if no random challenges
        page.challenges
          ? of(page)
          : this.challengeService.listChallenges(defaultQuery)
      ),
      map((page) => page.challenges)
    );
  }
}
