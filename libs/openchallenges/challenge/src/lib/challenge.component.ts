import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
// import {
//   Challenge,
// ChallengeService,
// BasicError as ApiClientBasicError,
// } from '@sagebionetworks/openchallenges/api-client-angular';
import {
  Challenge,
<<<<<<< HEAD
  ChallengeService,
  BasicError as ApiClientBasicError,
} from '@sagebionetworks/openchallenges/api-client-angular';
import {
  catchError,
  map,
  Observable,
  Subscription,
  switchMap,
  throwError,
} from 'rxjs';
import { Tab } from './tab.model';
import { CHALLENGE_TABS } from './challenge-tabs';
import { Avatar } from '@sagebionetworks/openchallenges/ui';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { isApiClientError } from '@sagebionetworks/openchallenges/util';
=======
  // ChallengeService,
  // BasicError as ApiClientBasicError,
} from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
import {
  // catchError,
  map,
  Observable,
  of,
  Subscription,
  // switchMap,
  // throwError,
} from 'rxjs';
import { Tab } from './tab.model';
import { CHALLENGE_TABS } from './challenge-tabs';
import {
  Avatar,
  DEPRECATED_MOCK_CHALLENGES,
} from '@sagebionetworks/openchallenges/ui';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
// import { isApiClientError } from '@sagebionetworks/openchallenges/util';
>>>>>>> 93bc819 (add codes to fix routes in challenge-profile)

@Component({
  selector: 'openchallenges-challenge',
  templateUrl: './challenge.component.html',
  styleUrls: ['./challenge.component.scss'],
})
export class ChallengeComponent implements OnInit {
  public appVersion: string;
  challenge$!: Observable<Challenge>;
  loggedIn = false;
  progressValue = 0;
  remainDays!: number | undefined;
  challengeAvatar!: Avatar;
  tabs = CHALLENGE_TABS;
  tabKeys: string[] = Object.keys(this.tabs);
  activeTab!: Tab;
  private subscriptions: Subscription[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
<<<<<<< HEAD
    private challengeService: ChallengeService,
=======
    // private challengeService: ChallengeService,
>>>>>>> 93bc819 (add codes to fix routes in challenge-profile)
    private readonly configService: ConfigService
  ) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit(): void {
<<<<<<< HEAD
    this.challenge$ = this.activatedRoute.params.pipe(
      switchMap((params) =>
        this.challengeService.getChallenge(params['challengeId'])
      ),
      // TODO: add challenge slug to url
      catchError((err) => {
        const error = err.error as ApiClientBasicError;
        if (isApiClientError(error) && error.status === 404) {
          // redirect to not-found for 404
          this.router.navigate(['/not-found']);
          return throwError(() => new Error(error.detail));
        } else {
          // redirect to org search for invalid url
          this.router.navigate(['/challenge']);
          return throwError(() => new Error(err.message));
        }
      })
    );
=======
    this.activatedRoute.params.subscribe((param) => {
      const challenge = DEPRECATED_MOCK_CHALLENGES.find(
        (c) => c.name === param['slug']
      );
      if (challenge) {
        this.challenge$ = of(challenge);
      }
    });
    // TODO: get chalenge using below chunk once
    // the `slug` property and `getChallenge` service are added
    // this.challenge$ = this.activatedRoute.params.pipe(
    //   switchMap((params) => this.challengeService.getChallenge(params['slug'])),
    //   catchError((err) => {
    //     const error = err.error as ApiClientBasicError;
    //     if (isApiClientError(error) && error.status === 404) {
    //       // redirect to not-found for 404
    //       this.router.navigate(['/not-found']);
    //       return throwError(() => new Error(error.detail));
    //     } else {
    //       // redirect to org search for invalid url
    //       this.router.navigate(['/challenge']);
    //       return throwError(() => new Error(err.message));
    //     }
    //   })
    // );
>>>>>>> 93bc819 (add codes to fix routes in challenge-profile)

    this.challenge$.subscribe((challenge) => {
      this.challengeAvatar = {
        name: challenge.name,
        src: 'https://via.placeholder.com/300.png', // TODO: Replace with avatarUrl once implemented in Challenge Object
        size: 250,
      };

      this.progressValue =
        challenge.startDate && challenge.endDate
          ? this.calcProgress(
              new Date().toUTCString(),
              challenge.startDate,
              challenge.endDate
            )
          : 0;

      this.remainDays = challenge.endDate
        ? this.calcDays(new Date().toUTCString(), challenge.endDate)
        : undefined;
    });

    const activeTabSub = this.activatedRoute.queryParamMap
      .pipe(
        map((params: ParamMap) => params.get('tab')),
        map((key) => (key === null ? 'overview' : key))
      )
      .subscribe((key) => (this.activeTab = this.tabs[key]));

    this.subscriptions.push(activeTabSub);
  }

  calcDays(startDate: string, endDate: string): number {
    const timeDiff = +new Date(endDate) - +new Date(startDate);
    return Math.round(timeDiff / (1000 * 60 * 60 * 24));
  }

  calcProgress(today: string, startDate: string, endDate: string): number {
    return (
      (this.calcDays(startDate, today) / this.calcDays(startDate, endDate)) *
      100
    );
  }
}
