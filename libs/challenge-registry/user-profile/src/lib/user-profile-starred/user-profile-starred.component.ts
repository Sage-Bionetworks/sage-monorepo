import { Component, Input, OnInit } from '@angular/core';
import { Challenge, Organization, User } from '@sagebionetworks/api-angular';
import { Tab } from '../tab.model';
import { USER_PROFILE_STARRED_TABS } from './user-profile-starred-tabs';
import {
  MOCK_CHALLENGES,
  MOCK_ORGANIZATIONS,
} from '@sagebionetworks/challenge-registry/ui';
import { Paginator } from 'primeng/paginator';
import { BehaviorSubject, of, switchMap } from 'rxjs';
import { assign } from 'lodash';

@Component({
  selector: 'challenge-registry-user-profile-starred',
  templateUrl: './user-profile-starred.component.html',
  styleUrls: ['./user-profile-starred.component.scss'],
})
export class UserProfileStarredComponent implements OnInit {
  @Input() user!: User;
  @Input() loggedIn!: boolean;
  // challenges: Challenge[] = MOCK_CHALLENGES;
  organizations: Organization[] = MOCK_ORGANIZATIONS;
  stars: Challenge[] = [];
  starred!: boolean;
  tabs = USER_PROFILE_STARRED_TABS;
  tabKeys: string[] = Object.keys(this.tabs);
  activeTab: Tab = this.tabs['challenges'];

  // mock up challenge/organization search results
  limit = 1;
  offset = 0;
  challengeResultsCount = 0;
  challenges!: Challenge[];

  private query: BehaviorSubject<any> = new BehaviorSubject<any>({
    offset: 0,
    limit: 1,
  });

  ngOnInit(): void {
    this.query
      .pipe(
        switchMap((query: any) =>
          of({
            challenges: []
              .concat(...new Array(100).fill(MOCK_CHALLENGES))
              .slice(query.offset, query.offset + query.limit),
            totalResults: 100 * MOCK_CHALLENGES.length,
          })
        )
      )
      .subscribe((page) => {
        this.challenges = page.challenges;
        console.log(this.challenges);
        this.challengeResultsCount = page.totalResults;
      });
  }

  updateQuery(): void {
    const query = assign(this.query.getValue(), {
      offset: this.offset,
      limit: this.limit,
    });
    this.query.next(query);
  }

  clickEvent(tab: string): void {
    this.activeTab = this.tabs[tab];
  }

  onPageChange(event: Paginator) {
    console.log(event.first);
    this.offset = event.first;
    console.log(event.rows);
    this.limit = event.rows;
    this.updateQuery();
  }
}
