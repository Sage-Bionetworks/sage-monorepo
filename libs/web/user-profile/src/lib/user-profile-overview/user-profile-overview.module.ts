import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserProfileOverviewComponent } from './user-profile-overview.component';

@NgModule({
  declarations: [UserProfileOverviewComponent],
  imports: [CommonModule],
  exports: [UserProfileOverviewComponent],
})
export class UserProfileOverviewModule {}
