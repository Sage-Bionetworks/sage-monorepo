import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/openchallenges/ui';
import { ChallengeOverviewComponent } from './challenge-overview.component';

@NgModule({
  declarations: [ChallengeOverviewComponent],
  imports: [CommonModule, UiModule],
  exports: [ChallengeOverviewComponent],
})
export class ChallengeOverviewModule {}
