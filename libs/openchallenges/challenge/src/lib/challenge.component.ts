import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
