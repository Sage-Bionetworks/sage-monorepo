import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Account } from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
import {
  catchError,
  map,
  Observable,
  Subscription,
  switchMap,
  throwError,
} from 'rxjs';
import { Tab } from './tab.model';
import { ORG_PROFILE_TABS } from './org-profile-tabs';
import { Avatar } from '@sagebionetworks/openchallenges/ui';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import {
  Organization,
  OrganizationService,
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
  account$!: Observable<Account | undefined>;
  organization$!: Observable<Organization>;
  loggedIn = true;
  organizationAvatar!: Avatar;
  tabs = ORG_PROFILE_TABS;
  tabKeys: string[] = Object.keys(this.tabs);
  activeTab!: Tab;
  private subscriptions: Subscription[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private readonly configService: ConfigService,
    private organizationService: OrganizationService
  ) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit(): void {
    // add more status codes if found
    this.organization$ = this.activatedRoute.params.pipe(
      switchMap((params) =>
        this.organizationService.getOrganization(params['orgLogin'])
      ),
      catchError((err) => {
        const errorMsg = handleHttpError(err, this.router, {
          404: '/not-found',
          400: '/org',
        } as HttpStatusRedirect);
        return throwError(() => errorMsg);
      })
    );

    this.organization$.subscribe(
      (org) =>
        (this.organizationAvatar = {
          name: org.name || org.login.replace(/-/g, ' '),
          src: org.avatarUrl || '',
          size: 250,
        })
    );

    const activeTabSub = this.activatedRoute.queryParamMap
      .pipe(
        map((params: ParamMap) => params.get('tab')),
        map((key) => (key === null ? 'overview' : key))
      )
      .subscribe((key) => (this.activeTab = this.tabs[key]));

    this.subscriptions.push(activeTabSub);
  }
}
