import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import {
  Account,
  AccountService,
  // ModelError as ApiClientError,
  User,
  UserService,
} from '@sagebionetworks/api-angular';
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
import { USER_PROFILE_TABS } from './user-profile-tabs';
import { MOCK_USER, Avatar } from '@sagebionetworks/challenge-registry/ui';
// import { MOCK_USER, MOCK_ORG } from '@sagebionetworks/challenge-registry/ui';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';

@Component({
  selector: 'challenge-registry-user',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
})
export class UserProfileComponent implements OnInit {
  public appVersion: string;
  account$!: Observable<Account | undefined>;
  user$: Observable<User> = of(MOCK_USER);
  loggedIn = true;
  userAvatar!: Avatar;
  tabs = USER_PROFILE_TABS;
  tabKeys: string[] = Object.keys(this.tabs);
  activeTab: Tab = this.tabs['overview'];

  private subscriptions: Subscription[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
    private userService: UserService,
    private authService: AuthService,
    private readonly configService: ConfigService
  ) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit(): void {
    const userProfile$ = this.activatedRoute.data.pipe(pluck('userProfile'));

    userProfile$.subscribe((userProfile) => {
      console.log('userProfile available to UserProfileComponent', userProfile);
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

    // this.user$ = this.account$.pipe(
    //   filter((account): account is Account => account !== undefined),
    //   switchMap((account) => this.userService.getUser(account.id))
    // );

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

    this.user$.subscribe(
      (user) =>
        (this.userAvatar = {
          name: user.name
            ? (user.name as string)
            : user.login.replace(/-/g, ' '),
          src: user.avatarUrl ? user.avatarUrl : '',
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
