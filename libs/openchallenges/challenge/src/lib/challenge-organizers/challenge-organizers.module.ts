import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/openchallenges/ui';
import { ChallengeOrganizersComponent } from './challenge-organizers.component';

@NgModule({
  declarations: [ChallengeOrganizersComponent],
  imports: [CommonModule, UiModule],
  exports: [ChallengeOrganizersComponent],
})
export class ChallengeOrganizersModule {}
