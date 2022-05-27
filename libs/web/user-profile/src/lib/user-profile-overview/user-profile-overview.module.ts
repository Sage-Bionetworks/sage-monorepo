import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WebUiModule } from '@sagebionetworks/web/ui';
import { UserProfileOverviewComponent } from './user-profile-overview.component';

@NgModule({
  declarations: [UserProfileOverviewComponent],
  imports: [CommonModule, WebUiModule],
  exports: [UserProfileOverviewComponent],
})
export class UserProfileOverviewModule {}
