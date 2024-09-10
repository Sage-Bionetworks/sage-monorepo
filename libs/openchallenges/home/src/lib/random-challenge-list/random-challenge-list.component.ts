import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { Observable, catchError, map, throwError } from 'rxjs';
import {
  Challenge,
  ChallengeService,
  ChallengeSearchQuery,
  ChallengeSort,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ChallengeCardComponent } from '@sagebionetworks/openchallenges/ui';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'openchallenges-random-challenge-list',
  standalone: true,
  imports: [CommonModule, MatButtonModule, ChallengeCardComponent, RouterModule],
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
      sortSeed: Math.round(+new Date().setHours(0, 0, 0, 0) / 1000), // daily seed
    };

    this.challenges$ = this.challengeService.listChallenges(query).pipe(
      catchError((err) => {
        return throwError(() => new Error(err.message));
      }),
      map((page) => page.challenges),
    );
  }
}
