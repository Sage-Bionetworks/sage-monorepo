import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserProfileBarModule } from './user-profile-bar/user-profile-bar.module';
import { UserProfileOverviewModule } from './user-profile-overview/user-profile-overview.module';

@NgModule({
  imports: [CommonModule],
  declarations: [],
  exports: [UserProfileBarModule, UserProfileOverviewModule],
})
export class WebFeatureUserProfileModule {}
