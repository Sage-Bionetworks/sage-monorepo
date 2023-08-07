import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Challenge,
  ChallengeService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import {
  catchError,
  // map,
  Observable,
  of,
  // Subscription,
  switchMap,
  throwError,
} from 'rxjs';
// import { Tab } from './tab.model';
// import { CHALLENGE_TABS } from './challenge-tabs';
import { Avatar } from '@sagebionetworks/openchallenges/ui';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import {
  HttpStatusRedirect,
  handleHttpError,
} from '@sagebionetworks/openchallenges/util';

@Component({
  selector: 'openchallenges-challenge',
  templateUrl: './challenge.component.html',
  styleUrls: ['./challenge.component.scss'],
})
export class ChallengeComponent implements OnInit {
  public appVersion: string;
  public dataUpdatedOn: string;
  challenge$!: Observable<Challenge>;
  loggedIn = false;
  // progressValue = 0;
  // remainDays!: number | undefined;
  challengeAvatar!: Avatar;
  // tabs = CHALLENGE_TABS;
  // tabKeys: string[] = Object.keys(this.tabs);
  // activeTab!: Tab;
  // private subscriptions: Subscription[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private challengeService: ChallengeService,
    private readonly configService: ConfigService
  ) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
  }

  ngOnInit(): void {
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

    this.challenge$.subscribe((challenge) => {
      this.challengeAvatar = {
        name: challenge.name,
        src: challenge.avatarUrl || '',
        size: 250,
      };

      // this.progressValue =
      //   challenge.startDate && challenge.endDate
      //     ? this.calcProgress(
      //         new Date().toUTCString(),
      //         challenge.startDate,
      //         challenge.endDate
      //       )
      //     : 0;

      // this.remainDays = challenge.endDate
      //   ? this.calcDays(new Date().toUTCString(), challenge.endDate)
      //   : undefined;
    });

    // const activeTabSub = this.activatedRoute.queryParamMap
    //   .pipe(
    //     map((params: ParamMap) => params.get('tab')),
    //     map((key) => (key === null ? 'overview' : key))
    //   )
    //   .subscribe((key) => (this.activeTab = this.tabs[key]));

    // this.subscriptions.push(activeTabSub);
  }

  // calcDays(startDate: string, endDate: string): number {
  //   const timeDiff = +new Date(endDate) - +new Date(startDate);
  //   return Math.round(timeDiff / (1000 * 60 * 60 * 24));
  // }

  // calcProgress(today: string, startDate: string, endDate: string): number {
  //   return (
  //     (this.calcDays(startDate, today) / this.calcDays(startDate, endDate)) *
  //     100
  //   );
  // }
}
