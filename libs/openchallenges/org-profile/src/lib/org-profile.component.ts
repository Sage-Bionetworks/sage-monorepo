import { Component, OnInit, Renderer2 } from '@angular/core';
import {
  ActivatedRoute,
  ParamMap,
  Router,
  RouterModule,
} from '@angular/router';
import {
  catchError,
  forkJoin,
  map,
  Observable,
  of,
  shareReplay,
  Subscription,
  switchMap,
  take,
  throwError,
} from 'rxjs';
import { Tab } from './tab.model';
import { ORG_PROFILE_TABS } from './org-profile-tabs';
import {
  Avatar,
  AvatarComponent,
  FooterComponent,
} from '@sagebionetworks/openchallenges/ui';
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
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
// import { MatTabsModule } from '@angular/material/tabs';
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { OrgProfileChallengesComponent } from './org-profile-challenges/org-profile-challenges.component';
import { OrgProfileMembersComponent } from './org-profile-members/org-profile-members.component';
import { OrgProfileOverviewComponent } from './org-profile-overview/org-profile-overview.component';
import { OrgProfileStatsComponent } from './org-profile-stats/org-profile-stats.component';
import { SeoService } from '@sagebionetworks/shared/util';
import { getSeoData } from './org-profile-seo-data';

@Component({
  selector: 'openchallenges-org-profile',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTabsModule,
    MatIconModule,
    OrgProfileOverviewComponent,
    OrgProfileChallengesComponent,
    OrgProfileMembersComponent,
    OrgProfileStatsComponent,
    AvatarComponent,
    FooterComponent,
  ],
  templateUrl: './org-profile.component.html',
  styleUrls: ['./org-profile.component.scss'],
})
export class OrgProfileComponent implements OnInit {
  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;

  organization$!: Observable<Organization>;
  organizationAvatar$!: Observable<Avatar>;
  loggedIn = true;
  // organizationAvatar!: Avatar;
  tabs = ORG_PROFILE_TABS;
  tabKeys: string[] = Object.keys(this.tabs);
  activeTab!: Tab;
  private subscriptions: Subscription[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private readonly configService: ConfigService,
    private organizationService: OrganizationService,
    private imageService: ImageService,
    private seoService: SeoService,
    private renderer2: Renderer2
  ) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
    this.privacyPolicyUrl = this.configService.config.privacyPolicyUrl;
    this.termsOfUseUrl = this.configService.config.termsOfUseUrl;
    this.apiDocsUrl = this.configService.config.apiDocsUrl;
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
      shareReplay(1),
      take(1)
    );

    this.organizationAvatar$ = this.organization$.pipe(
      switchMap((org) =>
        forkJoin({
          org: of(org),
          avatarUrl: this.getOrganizationImageUrl(org, ImageHeight._250px),
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

    const seoOrgImage$ = this.organization$.pipe(
      switchMap((org) => this.getOrganizationImageUrl(org, ImageHeight._500px))
    );

    const activeTabSub = this.activatedRoute.queryParamMap
      .pipe(
        map((params: ParamMap) => params.get('tab')),
        map((key) => (key === null ? 'overview' : key))
      )
      .subscribe((key) => (this.activeTab = this.tabs[key]));

    this.subscriptions.push(activeTabSub);

    forkJoin({
      org: this.organization$,
      image: seoOrgImage$,
    }).subscribe(({ org, image }) => {
      this.seoService.setData(getSeoData(org, image.url), this.renderer2);
    });
  }

  private getOrganizationImageUrl(
    org: Organization,
    height: ImageHeight
  ): Observable<Image> {
    return this.imageService
      .getImage({
        objectKey: org.avatarKey,
        height,
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
