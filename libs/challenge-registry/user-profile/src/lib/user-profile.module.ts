import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { UserProfileComponent } from './user-profile.component';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { UserProfileBarModule } from './user-profile-bar/user-profile-bar.module';
import { UserProfileChallengesModule } from './user-profile-challenges/user-profile-challenges.module';
import { UserProfileOverviewModule } from './user-profile-overview/user-profile-overview.module';
import { UserProfileStarredModule } from './user-profile-starred/user-profile-starred.module';
import { UserProfileStatsComponent } from './user-profile-stats/user-profile-stats.component';
import { UserProfileRoutingModule } from './user-profile-routing.module';

@NgModule({
  declarations: [UserProfileComponent, UserProfileStatsComponent],
  imports: [
    CommonModule,
    MatTabsModule,
    MatIconModule,
    UiModule,
    UserProfileRoutingModule,
    UserProfileBarModule,
    UserProfileChallengesModule,
    UserProfileOverviewModule,
    UserProfileStarredModule,
  ],
  exports: [UserProfileComponent, UserProfileStatsComponent],
})
export class UserProfileModule {}
