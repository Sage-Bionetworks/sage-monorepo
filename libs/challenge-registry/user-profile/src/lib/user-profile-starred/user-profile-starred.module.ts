import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { PaginatorModule } from 'primeng/paginator';
import { UserProfileStarredComponent } from './user-profile-starred.component';

@NgModule({
  declarations: [UserProfileStarredComponent],
  imports: [CommonModule, PaginatorModule, UiModule],
  exports: [UserProfileStarredComponent],
})
export class UserProfileStarredModule {}
