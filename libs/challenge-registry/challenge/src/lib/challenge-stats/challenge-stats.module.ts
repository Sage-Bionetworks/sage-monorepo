import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { ChallengeStatsComponent } from './challenge-stats.component';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [ChallengeStatsComponent],
  imports: [CommonModule, MatIconModule, UiModule],
  exports: [ChallengeStatsComponent],
})
export class ChallengeStatsModule {}
