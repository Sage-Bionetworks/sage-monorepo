import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/openchallenges/ui';
import { ChallengeSponsorsComponent } from './challenge-sponsors.component';

@NgModule({
  declarations: [ChallengeSponsorsComponent],
  imports: [CommonModule, UiModule],
  exports: [ChallengeSponsorsComponent],
})
export class ChallengeSponsorsModule {}
