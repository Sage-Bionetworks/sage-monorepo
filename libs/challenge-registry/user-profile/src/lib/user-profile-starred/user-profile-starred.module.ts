import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { UserProfileStarredComponent } from './user-profile-starred.component';

@NgModule({
  declarations: [UserProfileStarredComponent],
  imports: [CommonModule, UiModule],
  exports: [UserProfileStarredComponent],
})
export class UserProfileStarredModule {}
