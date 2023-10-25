// import { Component, Input, OnInit } from '@angular/core';
// import { User } from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
// import {
//   Challenge,
//   ChallengeSearchQuery,
//   ChallengeService,
// } from '@sagebionetworks/openchallenges/api-client-angular';
// import { Tab } from '../tab.model';
// import { USER_PROFILE_STARRED_TABS } from './user-profile-starred-tabs';
// import {
//   ChallengeCardComponent,
//   MOCK_ORGANIZATION_CARDS,
//   OrganizationCard,
//   OrganizationCardComponent,
//   PaginatorComponent,
// } from '@sagebionetworks/openchallenges/ui';
// import { BehaviorSubject, switchMap } from 'rxjs';
// import { assign } from 'lodash';
// import { CommonModule } from '@angular/common';
// import { PaginatorModule } from 'primeng/paginator';

// @Component({
//   selector: 'openchallenges-user-profile-starred',
//   standalone: true,
//   imports: [
//     CommonModule,
//     PaginatorModule,
//     OrganizationCardComponent,
//     PaginatorComponent,
//     ChallengeCardComponent,
//   ],
//   templateUrl: './user-profile-starred.component.html',
//   styleUrls: ['./user-profile-starred.component.scss'],
// })
// export class UserProfileStarredComponent implements OnInit {
//   @Input({ required: true }) user!: User;
//   @Input({ required: true }) loggedIn!: boolean;
//   organizationCards: OrganizationCard[] = MOCK_ORGANIZATION_CARDS;
//   stars: Challenge[] = [];
//   starred!: boolean;
//   tabs = USER_PROFILE_STARRED_TABS;
//   tabKeys: string[] = Object.keys(this.tabs);
//   activeTab: Tab = this.tabs['challenges'];

//   challenges: Challenge[] = [];
//   pageNumber = 0;
//   pageSize = 12;
//   totalChallengesCount = 0;

//   private query: BehaviorSubject<ChallengeSearchQuery> =
//     new BehaviorSubject<ChallengeSearchQuery>({});

//   constructor(private challengeService: ChallengeService) {}

//   ngOnInit(): void {
//     this.query
//       .pipe(
//         switchMap((query: ChallengeSearchQuery) =>
//           this.challengeService.listChallenges(query)
//         )
//       )
//       .subscribe((page) => {
//         this.totalChallengesCount = page.totalElements;
//         this.challenges = page.challenges.filter((c) => c.starredCount > 0);
//       });
//   }

//   onPageChange(event: any) {
//     const newQuery = assign(this.query.getValue(), {
//       pageNumber: event.page,
//       pageSize: event.rows,
//     });
//     this.query.next(newQuery);
//   }

//   clickEvent(tab: string): void {
//     this.activeTab = this.tabs[tab];
//   }
// }
