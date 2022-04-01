import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { WebUiModule } from '@challenge-registry/web/ui';
import { ChallengeComponent } from './challenge.component';
import { ChallengeRoutingModule } from './challenge-routing.modules';
import { ChallengeHeaderModule } from './challenge-header/challenge-header.module';
import { ChallengeStargazersModule } from './challenge-stargazers/challenge-stargazers.module';
import { ChallengeSettingsModule } from './challenge-settings/challenge-settings.module';

@NgModule({
  imports: [
    CommonModule,
    MatTabsModule,
    WebUiModule,
    ChallengeRoutingModule,
    ChallengeHeaderModule,
    ChallengeStargazersModule,
    ChallengeSettingsModule,
  ],
  declarations: [ChallengeComponent],
  exports: [ChallengeComponent],
})
export class ChallengeModule {}
