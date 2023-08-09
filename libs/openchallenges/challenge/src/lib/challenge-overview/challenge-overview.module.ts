import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/openchallenges/ui';
import { ChallengeOverviewComponent } from './challenge-overview.component';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [ChallengeOverviewComponent],
  imports: [CommonModule, MatIconModule, UiModule],
  exports: [ChallengeOverviewComponent],
})
export class ChallengeOverviewModule {}
