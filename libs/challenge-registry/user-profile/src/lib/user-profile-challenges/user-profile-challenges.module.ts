import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { PaginatorModule } from 'primeng/paginator';
import { UserProfileChallengesComponent } from './user-profile-challenges.component';

@NgModule({
  declarations: [UserProfileChallengesComponent],
  imports: [CommonModule, PaginatorModule, UiModule],
  exports: [UserProfileChallengesComponent],
})
export class UserProfileChallengesModule {}
