import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/openchallenges/ui';
import { ChallengeDetailsComponent } from './challenge-details.component';

@NgModule({
  declarations: [ChallengeDetailsComponent],
  imports: [CommonModule, UiModule],
  exports: [ChallengeDetailsComponent],
})
export class ChallengeDetailsModule {}
