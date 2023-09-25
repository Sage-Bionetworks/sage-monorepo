import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  Challenge,
  ChallengeService,
  ChallengeSearchQuery,
  ChallengeSort,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ChallengeCardComponent } from '@sagebionetworks/openchallenges/ui';
import { Observable, catchError, map, throwError } from 'rxjs';

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
    const query: ChallengeSearchQuery = {
      pageNumber: 0,
      pageSize: 3,
      searchTerms: '',
      sort: ChallengeSort.Random,
      // daily seed
      sortSeed: Math.round(+new Date().setHours(0, 0, 0, 0) / 1000),
    };

    const challengesPage$ = this.challengeService.listChallenges(query).pipe(
      catchError((err) => {
        return throwError(() => new Error(err.message));
      })
    );
    this.challenges$ = challengesPage$.pipe(map((page) => page.challenges));
  }
}
