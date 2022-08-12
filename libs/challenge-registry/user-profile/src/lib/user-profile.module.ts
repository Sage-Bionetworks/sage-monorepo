import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { UserProfileComponent } from './user-profile.component';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { UserProfileBarModule } from './user-profile-bar/user-profile-bar.module';
import { UserProfileChallengesModule } from './user-profile-challenges/user-profile-challenges.module';
import { UserProfileOverviewModule } from './user-profile-overview/user-profile-overview.module';
import { UserProfileStarredModule } from './user-profile-starred/user-profile-starred.module';
import { UserProfileStatsComponent } from './user-profile-stats/user-profile-stats.component';

const routes: Routes = [{ path: '', component: UserProfileComponent }];

@NgModule({
  declarations: [UserProfileComponent, UserProfileStatsComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatTabsModule,
    MatIconModule,
    UiModule,
    UserProfileBarModule,
    UserProfileChallengesModule,
    UserProfileOverviewModule,
    UserProfileStarredModule,
  ],
  exports: [UserProfileComponent, UserProfileStatsComponent],
})
export class UserProfileModule {}
