import { Component, Input } from '@angular/core';
import { Challenge, Organization, User } from '@sagebionetworks/api-angular';
import { Tab } from '../tab.model';
import { USER_PROFILE_STARRED_TABS } from './user-profile-starred-tabs';
import {
  MOCK_CHALLENGES,
  MOCK_ORGANIZATIONS,
} from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-user-profile-starred',
  templateUrl: './user-profile-starred.component.html',
  styleUrls: ['./user-profile-starred.component.scss'],
})
export class UserProfileStarredComponent {
  @Input() user!: User;
  @Input() loggedIn!: boolean;
  challenges: Challenge[] = MOCK_CHALLENGES;
  organizations: Organization[] = MOCK_ORGANIZATIONS;
  stars: Challenge[] = [];
  starred!: boolean;
  tabs = USER_PROFILE_STARRED_TABS;
  tabKeys: string[] = Object.keys(this.tabs);
  activeTab: Tab = this.tabs['challenges'];

  clickEvent(tab: string): void {
    this.activeTab = this.tabs[tab];
  }
}
