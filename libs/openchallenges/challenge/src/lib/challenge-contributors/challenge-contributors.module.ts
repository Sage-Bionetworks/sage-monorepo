import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/openchallenges/ui';
import { ChallengeContributorsComponent } from './challenge-contributors.component';

@NgModule({
  declarations: [ChallengeContributorsComponent],
  imports: [CommonModule, UiModule],
  exports: [ChallengeContributorsComponent],
})
export class ChallengeContributorsModule {}
