import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserProfileBarModule } from './user-profile-bar/user-profile-bar.module';
import { UserProfileChallengesModule } from './user-profile-challenges/user-profile-challenges.module';
import { UserProfileOverviewModule } from './user-profile-overview/user-profile-overview.module';

@NgModule({
  imports: [CommonModule],
  declarations: [],
  exports: [
    UserProfileBarModule,
    UserProfileChallengesModule,
    UserProfileOverviewModule,
  ],
})
export class WebFeatureUserProfileModule {}
