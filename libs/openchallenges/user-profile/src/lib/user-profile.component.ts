// import { Component, OnInit, Renderer2 } from '@angular/core';
// import {
//   ActivatedRoute,
//   ParamMap,
//   Router,
//   RouterModule,
// } from '@angular/router';
// import {
//   Account,
//   AccountService,
//   // BasicError as ApiClientError,
//   User,
//   UserService,
// } from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
// import { AuthService } from '@sagebionetworks/openchallenges/auth';
// // import { isApiClientError } from '@sagebionetworks/openchallenges/util';
// import {
//   // catchError,
//   // filter,
//   map,
//   Observable,
//   of,
//   pluck,
//   Subscription,
//   // switchMap,
//   // throwError,
// } from 'rxjs';
// import { Tab } from './tab.model';
// import { USER_PROFILE_TABS } from './user-profile-tabs';
// import {
//   MOCK_USER,
//   Avatar,
//   FooterComponent,
//   AvatarComponent,
// } from '@sagebionetworks/openchallenges/ui';
// // import { MOCK_USER, MOCK_ORG } from '@sagebionetworks/openchallenges/ui';
// import { ConfigService } from '@sagebionetworks/openchallenges/config';
// import { UserProfile } from './user-profile';
// import { SeoService } from '@sagebionetworks/shared/util';
// import { getUserProfileSeoData } from './user-profile-seo';
// import { CommonModule } from '@angular/common';
// import { MatIconModule } from '@angular/material/icon';
// // import { MatTabsModule } from '@angular/material/tabs';
// import {  MatTabsModule } from '@angular/material/tabs';
// import { UserProfileChallengesComponent } from './user-profile-challenges/user-profile-challenges.component';
// import { UserProfileOverviewComponent } from './user-profile-overview/user-profile-overview.component';
// import { UserProfileStarredComponent } from './user-profile-starred/user-profile-starred.component';
// import { UserProfileStatsComponent } from './user-profile-stats/user-profile-stats.component';

// @Component({
//   selector: 'openchallenges-user',
//   standalone: true,
//   imports: [
//     CommonModule,
//     MatTabsModule,
//     MatIconModule,
//     RouterModule,
//     UserProfileChallengesComponent,
//     UserProfileOverviewComponent,
//     UserProfileStarredComponent,
//     FooterComponent,
//     AvatarComponent,
//     UserProfileStatsComponent,
//   ],
//   templateUrl: './user-profile.component.html',
//   styleUrls: ['./user-profile.component.scss'],
// })
// export class UserProfileComponent implements OnInit {
//   public appVersion: string;
//   public dataUpdatedOn: string;
//   account$!: Observable<Account | undefined>;
//   user$: Observable<User> = of(MOCK_USER);
//   loggedIn = true;
//   userAvatar!: Avatar;
//   tabs = USER_PROFILE_TABS;
//   tabKeys: string[] = Object.keys(this.tabs);
//   activeTab: Tab = this.tabs['overview'];

//   private subscriptions: Subscription[] = [];

//   constructor(
//     private activatedRoute: ActivatedRoute,
//     private router: Router,
//     private accountService: AccountService,
//     private userService: UserService,
//     private authService: AuthService,
//     private readonly configService: ConfigService,
//     private seoService: SeoService,
//     private renderer2: Renderer2
//   ) {
//     this.appVersion = this.configService.config.appVersion;
//     this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
//   }

//   ngOnInit(): void {
//     const userProfile$: Observable<UserProfile> = this.activatedRoute.data.pipe(
//       pluck('userProfile')
//     );

//     // this.router.events
//     //   .pipe(
//     //     filter((e) => e instanceof NavigationEnd),
//     //     map(() => this.activatedRoute),
//     //     map((route) => {
//     //       while (route.firstChild) route = route.firstChild;
//     //       return route;
//     //     }),
//     //     filter((route) => route.outlet === 'primary'),
//     //     mergeMap((route) => route.data)
//     //   )
//     //   .subscribe((data) => {
//     //     if ('seo' in data) {
//     //       const seoData = data['seo'];
//     //       if (Object.prototype.hasOwnProperty.call(seoData, 'title')) {
//     //         this.seoService.updateTitle(seoData['title']);
//     //       }
//     //       if (Object.prototype.hasOwnProperty.call(seoData, 'metaTags')) {
//     //         this.seoService.updateMetaTags(seoData['metaTags']);
//     //       }
//     //     }
//     //   });

//     userProfile$.subscribe((userProfile) => {
//       console.log('userProfile available to UserProfileComponent', userProfile);
//       this.seoService.setData(
//         getUserProfileSeoData(userProfile),
//         this.renderer2
//       );
//     });

//     // this.account$ = this.route.params.pipe(
//     //   switchMap((params) => this.accountService.getAccount(params['login'])),
//     //   catchError((err) => {
//     //     const error = err.error as ApiClientError;
//     //     if (isApiClientError(error)) {
//     //       if (error.status === 404) {
//     //         return of(undefined);
//     //       }
//     //     }
//     //     return throwError(err);
//     //   })
//     // );

//     // this.account$.subscribe((account) => {
//     //   console.log(account);
//     //   const pageTitle = account ? `${account.login}` : 'Page not found';
//     //   console.log(pageTitle);
//     //   // this.pageTitleService.setTitle(`${pageTitle} · ROCC`);
//     //   // this.accountNotFound = !account;
//     // });

//     // this.user$ = this.account$.pipe(
//     //   filter((account): account is Account => account !== undefined),
//     //   switchMap((account) => this.userService.getUser(account.id))
//     // );

//     // const orgs$ = this.account$.pipe(
//     //   filter((account): account is Account => account !== undefined),
//     //   switchMap((account) =>
//     //     this.userService.listUserOrganizations(account.id)
//     //   ),
//     //   map((page) => page.organizations)
//     // );

//     const activeTab$ = this.activatedRoute.queryParamMap.pipe(
//       map((params: ParamMap) => params.get('tab')),
//       map((key) => (key === null ? 'overview' : key))
//     );

//     this.user$.subscribe(
//       (user) =>
//         (this.userAvatar = {
//           name: user.name
//             ? (user.name as string)
//             : user.login.replace(/-/g, ' '),
//           src: user.avatarUrl ? user.avatarUrl : '',
//           size: 250,
//         })
//     );

//     // const orgsSub = orgs$.subscribe((orgs) => (this.organizations = orgs));

//     const activeTabSub = activeTab$.subscribe((key) => {
//       if (!this.tabKeys.includes(key)) {
//         this.router.navigate([]);
//       } else {
//         this.activeTab = this.tabs[key];
//       }
//     });

//     // this.authService
//     //   .isLoggedIn()
//     //   .subscribe((loggedIn) => (this.loggedIn = loggedIn));

//     // this.subscriptions.push(orgsSub);
//     this.subscriptions.push(activeTabSub);
//   }
// }
