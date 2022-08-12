import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { UserProfileChallengesComponent } from './user-profile-challenges.component';

@NgModule({
  declarations: [UserProfileChallengesComponent],
  imports: [CommonModule, UiModule],
  exports: [UserProfileChallengesComponent],
})
export class UserProfileChallengesModule {}
