import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Account } from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';
import {
  catchError,
  map,
  Observable,
  of,
  Subscription,
  switchMap,
  throwError,
} from 'rxjs';
import { Tab } from './tab.model';
import { ORG_PROFILE_TABS } from './org-profile-tabs';
import {
  MOCK_ORGANIZATIONS,
  Avatar,
} from '@sagebionetworks/challenge-registry/ui';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';
import {
  Organization,
  OrganizationService,
  OrganizationsPage,
  BasicError as ApiClientBasicError,
} from '@sagebionetworks/challenge-registry/api-client-angular';
import { isApiClientError } from '@sagebionetworks/challenge-registry/util';

@Component({
  selector: 'challenge-registry-org-profile',
  templateUrl: './org-profile.component.html',
  styleUrls: ['./org-profile.component.scss'],
})
export class OrgProfileComponent implements OnInit {
  public appVersion: string;
  account$!: Observable<Account | undefined>;
  organization$: Observable<Organization> = of(MOCK_ORGANIZATIONS[0]);
  loggedIn = true;
  organizationAvatar!: Avatar;
  tabs = ORG_PROFILE_TABS;
  tabKeys: string[] = Object.keys(this.tabs);
  activeTab: Tab = this.tabs['overview'];
  private subscriptions: Subscription[] = [];
  private useLocalOrganizationMock = false;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private route: ActivatedRoute,
    private readonly configService: ConfigService,
    private organizationService: OrganizationService
  ) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit(): void {
    if (!this.useLocalOrganizationMock) {
      this.organization$ = this.route.params.pipe(
        switchMap((params) =>
          this.organizationService.getOrganization(params['orgLogin'])
        ),
        catchError((err) => {
          const error = err.error as ApiClientBasicError;
          if (isApiClientError(error)) {
            if (error.status === 404) {
              // TODO: Redirect to org not found page or component
              // return of(undefined);
            }
          }
          return throwError(() => new Error('test'));
        })
      );
    }

    this.route.params.subscribe((param) => console.log(param['orgLogin']));

    const activeTab$ = this.activatedRoute.queryParamMap.pipe(
      map((params: ParamMap) => params.get('tab')),
      map((key) => (key === null ? 'overview' : key))
    );

    this.organization$.subscribe(
      (org) =>
        (this.organizationAvatar = {
          name: org.name ? (org.name as string) : org.login.replace(/-/g, ' '),
          src: org.avatarUrl ? org.avatarUrl : '',
          size: 250,
        })
    );

    const activeTabSub = activeTab$.subscribe((key) => {
      if (!this.tabKeys.includes(key)) {
        this.router.navigate([]);
      } else {
        this.activeTab = this.tabs[key];
      }
    });

    this.subscriptions.push(activeTabSub);

    // demo: get list of organizations from the new backend.
    this.organizationService
      .listOrganizations()
      .subscribe((page: OrganizationsPage) =>
        console.log('Organizations page', page)
      );
  }
}
