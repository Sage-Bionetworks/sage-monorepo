import { Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Challenge,
  ChallengeService,
  ModelError as ApiClientError,
} from '@challenge-registry/api-angular';
import { APP_CONFIG, AppConfig } from '@challenge-registry/web/config';
import { catchError, Observable, of, switchMap, throwError } from 'rxjs';
import { isApiClientError } from '@challenge-registry/web/util';
import { CHALLENGE_SECTIONS } from './challenge-sections';
import { ChallengeDataService } from './challenge-data.service';

@Component({
  selector: 'challenge-registry-challenges',
  templateUrl: './challenge.component.html',
  styleUrls: ['./challenge.component.scss'],
})
export class ChallengeComponent implements OnInit {
  public appVersion: string;

  challenge$!: Observable<Challenge | undefined>;
  challengeNotFound = false;
  accountName = '';

  sections: any[] = [];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private challengeService: ChallengeService,
    private challengeDataService: ChallengeDataService,
    @Inject(APP_CONFIG) private appConfig: AppConfig
  ) {
    this.appVersion = appConfig.appVersion;
  }

  ngOnInit(): void {
    this.sections = CHALLENGE_SECTIONS;

    this.challenge$ = this.route.params.pipe(
      switchMap((params) => {
        this.challengeDataService.setLogin(params['login']);
        return this.challengeDataService.fetchChallenge(
          params['login'],
          params['challenge']
        );
      }),
      catchError((err) => {
        const error = err.error as ApiClientError;
        if (isApiClientError(error)) {
          if (error.status === 404) {
            return of(undefined);
          }
        }
        return throwError(err);
      })
    );

    // const starred$ = this.authService.isSignedIn().pipe(
    //   filter((isSignedIn) => isSignedIn),
    //   switchMap(() => this.challengeDataService.fetchStarred())
    // );

    // starred$.subscribe();

    // this.challenge$.subscribe((challenge) => {
    //   const pageTitle = challenge ? `${challenge.name}` : 'Page not found';
    //   this.pageTitleService.setTitle(`${pageTitle} · ROCC`);
    //   this.challengeNotFound = !challenge;
    // });
  }
}
