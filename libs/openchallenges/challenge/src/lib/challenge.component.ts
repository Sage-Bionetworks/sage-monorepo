import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import {
  Challenge,
  ChallengeService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import {
  catchError,
  Observable,
  of,
  Subscription,
  switchMap,
  throwError,
} from 'rxjs';
import { CHALLENGE_LINKS } from './challenge-links';
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
import { CommonModule } from '@angular/common';

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
export class ChallengeComponent implements OnInit {
  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;

  challenge$!: Observable<Challenge>;
  loggedIn = false;
  challengeAvatar!: Avatar;
  private subscriptions: Subscription[] = [];
  rootUrl = this.router.url.split('#')[0];
  links = CHALLENGE_LINKS;
  public activeLink = 'overview';

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private challengeService: ChallengeService,
    private readonly configService: ConfigService
  ) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
    this.privacyPolicyUrl = this.configService.config.privacyPolicyUrl;
    this.termsOfUseUrl = this.configService.config.termsOfUseUrl;
  }

  ngOnInit(): void {
    this.challenge$ = this.activatedRoute.params.pipe(
      switchMap((params) =>
        this.challengeService.getChallenge(params['challengeId'])
      ),
      switchMap((challenge) => {
        this.router.navigate(['/challenge', challenge.id, challenge.slug]);
        // Resetting here, to account for when the page refreshes or when the
        // link is shared with the challenge name in the url already
        this.rootUrl = this.router.url.split('#')[0];
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
        src: challenge.avatarUrl ?? '',
        size: 250,
      };
    });

    this.subscriptions.push(
      this.activatedRoute.fragment.subscribe((fragment) => {
        if (fragment != null) {
          this.activeLink = fragment;
          const target = document.getElementById(this.activeLink);
          if (target) {
            target.scrollIntoView();
          }
        }
      })
    );
  }
}
