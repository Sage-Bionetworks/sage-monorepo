import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Challenge,
  ChallengeService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { catchError, Observable, of, switchMap, throwError } from 'rxjs';
import {
  HttpStatusRedirect,
  handleHttpError,
} from '@sagebionetworks/openchallenges/util';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/openchallenges/ui';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'openchallenges-challenge-stats',
  standalone: true,
  imports: [CommonModule, MatIconModule, UiModule],
  templateUrl: './challenge-stats.component.html',
  styleUrls: ['./challenge-stats.component.scss'],
})
export class ChallengeStatsComponent implements OnInit {
  @Input() loggedIn = false;
  challenge$!: Observable<Challenge>;
  mockViews!: number;
  mockStargazers!: number;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private challengeService: ChallengeService
  ) {}

  ngOnInit(): void {
    this.mockViews = 5_000;
    this.mockStargazers = 2;

    this.challenge$ = this.activatedRoute.params.pipe(
      switchMap((params) =>
        this.challengeService.getChallenge(params['challengeId'])
      ),
      switchMap((challenge) => {
        this.router.navigate(['/challenge', challenge.id, challenge.slug]);
        return of(challenge);
      }),
      catchError((err) => {
        const error = handleHttpError(err, this.router, {
          404: '/not-found',
          400: '/challenge',
        } as HttpStatusRedirect);
        return throwError(() => error);
      })
    );
  }

  shorthand(n: number | undefined) {
    if (n) {
      return Intl.NumberFormat('en-US', {
        maximumFractionDigits: 1,
      }).format(n);
    } else {
      return 0;
    }
  }
}
