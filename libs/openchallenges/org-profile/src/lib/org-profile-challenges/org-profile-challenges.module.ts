import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/openchallenges/ui';
import { OrgProfileChallengesComponent } from './org-profile-challenges.component';

@NgModule({
  declarations: [OrgProfileChallengesComponent],
  imports: [CommonModule, UiModule],
  exports: [OrgProfileChallengesComponent],
})
export class OrgProfileChallengesModule {}
