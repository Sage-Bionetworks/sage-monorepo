import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { UserProfileOverviewComponent } from './user-profile-overview.component';

@NgModule({
  declarations: [UserProfileOverviewComponent],
  imports: [CommonModule, UiModule],
  exports: [UserProfileOverviewComponent],
})
export class UserProfileOverviewModule {}
