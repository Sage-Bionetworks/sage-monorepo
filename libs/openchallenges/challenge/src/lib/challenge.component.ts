import { Component, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import {
  Challenge,
  ChallengeService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import {
  catchError,
  combineLatest,
  map,
  Observable,
  shareReplay,
  Subscription,
  switchMap,
  take,
  throwError,
} from 'rxjs';
import { Tab } from './tab.model';
import { CHALLENGE_TABS } from './challenge-tabs';
import {
  Avatar,
  AvatarComponent,
  FooterComponent,
} from '@sagebionetworks/openchallenges/ui';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import {
  HttpStatusRedirect,
  handleHttpError,
} from '@sagebionetworks/openchallenges/util';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { ChallengeContributorsComponent } from './challenge-contributors/challenge-contributors.component';
import { ChallengeOrganizersComponent } from './challenge-organizers/challenge-organizers.component';
import { ChallengeOverviewComponent } from './challenge-overview/challenge-overview.component';
import { ChallengeStargazersComponent } from './challenge-stargazers/challenge-stargazers.component';
import { ChallengeStatsComponent } from './challenge-stats/challenge-stats.component';
import { CommonModule, Location } from '@angular/common';
import { SeoService } from '@sagebionetworks/shared/util';
import { getSeoData } from './challenge-seo-data';
import { HttpParams } from '@angular/common/http';

@Component({
  selector: 'openchallenges-challenge',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTabsModule,
    MatIconModule,
    ChallengeOverviewComponent,
    ChallengeOrganizersComponent,
    ChallengeContributorsComponent,
    ChallengeStargazersComponent,
    ChallengeStatsComponent,
    AvatarComponent,
    FooterComponent,
  ],
  templateUrl: './challenge.component.html',
  styleUrls: ['./challenge.component.scss'],
})
export class ChallengeComponent implements OnInit, OnDestroy {
  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;

  challenge$!: Observable<Challenge>;
  loggedIn = false;
  challengeAvatar!: Avatar;
  tabs = CHALLENGE_TABS;
  activeTab!: Tab;
  private subscription = new Subscription();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private challengeService: ChallengeService,
    private readonly configService: ConfigService,
    private seoService: SeoService,
    private renderer2: Renderer2,
    private _location: Location,
  ) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
    this.privacyPolicyUrl = this.configService.config.privacyPolicyUrl;
    this.termsOfUseUrl = this.configService.config.termsOfUseUrl;
    this.apiDocsUrl = this.configService.config.apiDocsUrl;
  }

  ngOnInit(): void {
    this.challenge$ = this.activatedRoute.params.pipe(
      switchMap((params) =>
        this.challengeService.getChallenge(params['challengeId']),
      ),
      catchError((err) => {
        const error = handleHttpError(err, this.router, {
          404: '/not-found',
          400: '/challenge',
        } as HttpStatusRedirect);
        return throwError(() => error);
      }),
      shareReplay(1),
      take(1),
    );

    this.challenge$.subscribe((challenge) => {
      this.challengeAvatar = {
        name: challenge.name,
        src: challenge.avatarUrl ?? '',
        size: 250,
      };

      this.seoService.setData(getSeoData(challenge), this.renderer2);
    });

    const activeTabKey$: Observable<string> =
      this.activatedRoute.queryParams.pipe(
        map((params) =>
          Object.keys(this.tabs).includes(params['tab'])
            ? params['tab']
            : 'overview',
        ),
      );

    const combineSub = combineLatest({
      challenge: this.challenge$,
      activeTabKey: activeTabKey$,
    }).subscribe(({ challenge, activeTabKey }) => {
      // add slug in url and active param if any
      const newPath = `/challenge/${challenge.id}/${challenge.slug}`;
      this.updateTab(activeTabKey, newPath);
    });

    this.subscription.add(combineSub);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  updateTab(activeTabKey: string, path?: string) {
    // update tab param in the url
    const queryParams = { tab: activeTabKey };
    const newParam = new HttpParams({
      fromObject: queryParams,
    });
    const newPath = path ?? location.pathname;
    this._location.replaceState(newPath, newParam.toString());

    // update active tab
    this.activeTab = this.tabs[activeTabKey];
  }
}
