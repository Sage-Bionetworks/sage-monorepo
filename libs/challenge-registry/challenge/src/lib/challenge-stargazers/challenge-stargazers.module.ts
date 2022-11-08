import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { ChallengeStargazersComponent } from './challenge-stargazers.component';

@NgModule({
  declarations: [ChallengeStargazersComponent],
  imports: [CommonModule, UiModule],
  exports: [ChallengeStargazersComponent],
})
export class ChallengeStargazersModule {}
