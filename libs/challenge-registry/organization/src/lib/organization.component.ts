import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import {
  Account,
  AccountService,
  Organization,
  OrganizationService,
  // ModelError as ApiClientError,
} from '@sagebionetworks/api-client-angular-deprecated';
import { AuthService } from '@sagebionetworks/challenge-registry/auth';
// import { isApiClientError } from '@sagebionetworks/challenge-registry/util';
import {
  // catchError,
  // filter,
  map,
  Observable,
  of,
  pluck,
  Subscription,
  // switchMap,
  // throwError,
} from 'rxjs';
import { Tab } from './tab.model';
import { ORGANIZATION_TABS } from './organization-tabs';
import {
  MOCK_ORGANIZATIONS,
  Avatar,
} from '@sagebionetworks/challenge-registry/ui';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';

@Component({
  selector: 'challenge-registry-organization',
  templateUrl: './organization.component.html',
  styleUrls: ['./organization.component.scss'],
})
export class OrganizationComponent implements OnInit {
  public appVersion: string;
  account$!: Observable<Account | undefined>;
  organization$: Observable<Organization> = of(MOCK_ORGANIZATIONS[0]);
  loggedIn = true;
  organizationAvatar!: Avatar;
  tabs = ORGANIZATION_TABS;
  tabKeys: string[] = Object.keys(this.tabs);
  activeTab: Tab = this.tabs['overview'];

  private subscriptions: Subscription[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
    private organizationService: OrganizationService,
    private authService: AuthService,
    private readonly configService: ConfigService
  ) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit(): void {
    const userProfile$: Observable<Organization> =
      this.activatedRoute.data.pipe(pluck('organization'));

    userProfile$.subscribe((org) => {
      console.log('Organization available to OrganizationComponent', org);
    });

    // this.account$ = this.route.params.pipe(
    //   switchMap((params) => this.accountService.getAccount(params['login'])),
    //   catchError((err) => {
    //     const error = err.error as ApiClientError;
    //     if (isApiClientError(error)) {
    //       if (error.status === 404) {
    //         return of(undefined);
    //       }
    //     }
    //     return throwError(err);
    //   })
    // );

    // this.account$.subscribe((account) => {
    //   console.log(account);
    //   const pageTitle = account ? `${account.login}` : 'Page not found';
    //   console.log(pageTitle);
    //   // this.pageTitleService.setTitle(`${pageTitle} · ROCC`);
    //   // this.accountNotFound = !account;
    // });

    // const orgs$ = this.account$.pipe(
    //   filter((account): account is Account => account !== undefined),
    //   switchMap((account) =>
    //     this.userService.listUserOrganizations(account.id)
    //   ),
    //   map((page) => page.organizations)
    // );

    const activeTab$ = this.activatedRoute.queryParamMap.pipe(
      map((params: ParamMap) => params.get('tab')),
      map((key) => (key === null ? 'overview' : key))
    );

    this.organization$.subscribe(
      (org) =>
        (this.organizationAvatar = {
          name: org.name ? (org.name as string) : org.login.replace(/-/g, ' '),
          src: org.avatarUrl ? org.avatarUrl : '',
          size: 320,
        })
    );

    // const orgsSub = orgs$.subscribe((orgs) => (this.organizations = orgs));

    const activeTabSub = activeTab$.subscribe((key) => {
      if (!this.tabKeys.includes(key)) {
        this.router.navigate([]);
      } else {
        this.activeTab = this.tabs[key];
      }
    });

    // this.authService
    //   .isLoggedIn()
    //   .subscribe((loggedIn) => (this.loggedIn = loggedIn));

    // this.subscriptions.push(orgsSub);
    this.subscriptions.push(activeTabSub);
  }
}
