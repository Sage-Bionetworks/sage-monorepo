import { Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import {
  Account,
  AccountService,
  ModelError as ApiClientError,
  Organization,
  User,
  UserService,
} from '@sagebionetworks/api-angular';
import { AuthService } from '@sagebionetworks/web/auth';
import { AppConfig, APP_CONFIG } from '@sagebionetworks/web/config';
import { isApiClientError } from '@sagebionetworks/web/util';
import {
  catchError,
  filter,
  map,
  Observable,
  of,
  Subscription,
  switchMap,
  throwError,
} from 'rxjs';
import { Tab } from './tab.model';
import { USER_PROFILE_TABS } from './user-profile-tabs';

@Component({
  selector: 'challenge-registry-user',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
})
export class UserProfileComponent implements OnInit {
  public appVersion: string;
  account$!: Observable<Account | undefined>;
  user$!: Observable<User>;
  orgs: Organization[] = [];
  loggedIn = false;

  tabs = USER_PROFILE_TABS;
  tabKeys: string[] = Object.keys(this.tabs);
  activeTab: Tab = this.tabs['overview'];

  private subscriptions: Subscription[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
    private userService: UserService,
    private authService: AuthService,
    @Inject(APP_CONFIG) private appConfig: AppConfig
  ) {
    this.appVersion = appConfig.appVersion;
  }

  ngOnInit(): void {
    this.account$ = this.route.params.pipe(
      switchMap((params) => this.accountService.getAccount(params['login'])),
      catchError((err) => {
        const error = err.error as ApiClientError;
        if (isApiClientError(error)) {
          if (error.status === 404) {
            return of(undefined);
          }
        }
        return throwError(err);
      })
    );

    this.account$.subscribe((account) => {
      const pageTitle = account ? `${account.login}` : 'Page not found';
      console.log(pageTitle);
      // this.pageTitleService.setTitle(`${pageTitle} · ROCC`);
      // this.accountNotFound = !account;
    });

    this.user$ = this.account$.pipe(
      filter((account): account is Account => account !== undefined),
      switchMap((account) => this.userService.getUser(account.id))
    );

    const orgs$ = this.account$.pipe(
      filter((account): account is Account => account !== undefined),
      switchMap((account) =>
        this.userService.listUserOrganizations(account.id)
      ),
      map((page) => page.organizations)
    );

    const activeTab$ = this.route.queryParamMap.pipe(
      map((params: ParamMap) => params.get('tab')),
      map((key) => (key === null ? 'overview' : key))
    );

    const orgsSub = orgs$.subscribe((orgs) => (this.orgs = orgs));

    const activeTabSub = activeTab$.subscribe((key) => {
      if (!this.tabKeys.includes(key)) {
        this.router.navigate([]);
      } else {
        this.activeTab = this.tabs[key];
      }
    });

    this.authService
      .isLoggedIn()
      .subscribe((loggedIn) => (this.loggedIn = loggedIn));

    this.subscriptions.push(orgsSub);
    this.subscriptions.push(activeTabSub);
  }
}
