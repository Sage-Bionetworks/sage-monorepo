import { Component, OnInit, Renderer2 } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import {
  Account,
  Challenge,
} from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
import { map, Observable, of, Subscription } from 'rxjs';
import { Tab } from './tab.model';
import { CHALLENGE_TABS } from './challenge-tabs';
import {
  DEPRECATED_MOCK_CHALLENGES,
  Avatar,
} from '@sagebionetworks/openchallenges/ui';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { getChallengeSeoData } from './challenge-seo';
import { SeoService } from '@sagebionetworks/shared/util';

@Component({
  selector: 'openchallenges-challenge',
  templateUrl: './challenge.component.html',
  styleUrls: ['./challenge.component.scss'],
})
export class ChallengeComponent implements OnInit {
  public appVersion: string;
  account$!: Observable<Account | undefined>;
  challenge$!: Observable<Challenge>;
  loggedIn = false;
  progressValue = 0;
  remainDays!: number | undefined;
  challengeAvatar!: Avatar;
  tabs = CHALLENGE_TABS;
  tabKeys: string[] = Object.keys(this.tabs);
  activeTab: Tab = this.tabs['overview'];
  private subscriptions: Subscription[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private readonly configService: ConfigService,
    private seoService: SeoService,
    private renderer2: Renderer2
  ) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((param) => {
      const challenge = DEPRECATED_MOCK_CHALLENGES.find(
        (c) => c.name === param['challengeName']
      );
      if (challenge) {
        this.challenge$ = of(challenge);
      }
    });

    const activeTab$ = this.activatedRoute.queryParamMap.pipe(
      map((params: ParamMap) => params.get('tab')),
      map((key) => (key === null ? 'overview' : key))
    );

    this.challenge$.subscribe((challenge) => {
      this.challengeAvatar = {
        name: challenge.displayName || challenge.name.replace(/-/g, ' '),
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

      this.seoService.setData(getChallengeSeoData(challenge), this.renderer2);
    });

    const activeTabSub = activeTab$.subscribe((key) => {
      if (!this.tabKeys.includes(key)) {
        this.router.navigate([]);
      } else {
        this.activeTab = this.tabs[key];
      }
    });

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
