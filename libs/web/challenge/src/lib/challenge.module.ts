import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { WebUiModule } from '@challenge-registry/web/ui';
import { ChallengeComponent } from './challenge.component';
import { ChallengeRoutingModule } from './challenge-routing.modules';
import { ChallengeHeaderModule } from './challenge-header/challenge-header.module';
import { ChallengeStargazersModule } from './challenge-stargazers/challenge-stargazers.module';

@NgModule({
  imports: [
    CommonModule,
    MatTabsModule,
    WebUiModule,
    ChallengeRoutingModule,
    ChallengeHeaderModule,
    ChallengeStargazersModule,
  ],
  declarations: [ChallengeComponent],
  exports: [ChallengeComponent],
})
export class ChallengeModule {}
