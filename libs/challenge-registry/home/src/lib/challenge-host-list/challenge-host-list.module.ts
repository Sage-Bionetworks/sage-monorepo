import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { ChallengeHostListComponent } from './challenge-host-list.component';

@NgModule({
  declarations: [ChallengeHostListComponent],
  imports: [CommonModule, UiModule],
  exports: [ChallengeHostListComponent],
})
export class ChallengeHostListModule {}
