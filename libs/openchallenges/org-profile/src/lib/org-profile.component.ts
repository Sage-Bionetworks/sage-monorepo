import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Account } from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
import {
  catchError,
  forkJoin,
  map,
  Observable,
  of,
  shareReplay,
  Subscription,
  switchMap,
  throwError,
} from 'rxjs';
import { ORG_PROFILE_LINKS } from './org-profile-links';
import { Avatar } from '@sagebionetworks/openchallenges/ui';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import {
  ImageAspectRatio,
  ImageHeight,
  ImageQuery,
  ImageService,
  Organization,
  OrganizationService,
  Image,
} from '@sagebionetworks/openchallenges/api-client-angular';
import {
  HttpStatusRedirect,
  handleHttpError,
} from '@sagebionetworks/openchallenges/util';

@Component({
  selector: 'openchallenges-org-profile',
  templateUrl: './org-profile.component.html',
  styleUrls: ['./org-profile.component.scss'],
})
export class OrgProfileComponent implements OnInit {
  public appVersion: string;
  public dataUpdatedOn: string;
  account$!: Observable<Account | undefined>;
  organization$!: Observable<Organization>;
  organizationAvatar$!: Observable<Avatar>;
  loggedIn = true;
  // organizationAvatar!: Avatar;
  private subscriptions: Subscription[] = [];
  rootUrl = this.router.url.split('#')[0];
  links = ORG_PROFILE_LINKS;
  public activeLink = 'overview';

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private readonly configService: ConfigService,
    private organizationService: OrganizationService,
    private imageService: ImageService
  ) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
  }

  ngOnInit(): void {
    // add more status codes if found
    this.organization$ = this.activatedRoute.params.pipe(
      switchMap((params) =>
        this.organizationService.getOrganization(params['orgLogin'])
      ),
      catchError((err) => {
        const error = handleHttpError(err, this.router, {
          404: '/not-found',
          400: '/org',
        } as HttpStatusRedirect);
        return throwError(() => error);
      }),
      shareReplay(1)
    );

    this.organizationAvatar$ = this.organization$.pipe(
      switchMap((org) =>
        forkJoin({
          org: of(org),
          avatarUrl: this.getOrganizationAvatarUrl(org),
        })
      ),
      map(
        ({ org, avatarUrl }) =>
          ({
            name: org.name,
            src: avatarUrl?.url,
            size: 250,
          } as Avatar)
      )
    );

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

  private getOrganizationAvatarUrl(org: Organization): Observable<Image> {
    return this.imageService
      .getImage({
        objectKey: org.avatarKey,
        height: ImageHeight._250px,
        aspectRatio: ImageAspectRatio._11,
      } as ImageQuery)
      .pipe(
        catchError(() => {
          console.error(
            'Unable to get the image url. Please check the logs of the image service.'
          );
          return of({ url: '' });
        })
      );
  }
}
