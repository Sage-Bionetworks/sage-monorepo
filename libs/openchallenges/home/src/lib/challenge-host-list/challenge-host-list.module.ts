import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/openchallenges/ui';
import { ChallengeHostListComponent } from './challenge-host-list.component';
import { MatButtonModule } from '@angular/material/button';

@NgModule({
  declarations: [ChallengeHostListComponent],
  imports: [CommonModule, UiModule, MatButtonModule],
  exports: [ChallengeHostListComponent],
})
export class ChallengeHostListModule {}
