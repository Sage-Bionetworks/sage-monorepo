import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { ChallengeComponent } from './challenge.component';
import { ChallengeRoutingModule } from './challenge-routing.modules';
import { ChallengeHeaderModule } from './challenge-header/challenge-header.module';
import { ChallengeStargazersModule } from './challenge-stargazers/challenge-stargazers.module';
import { ChallengeSettingsModule } from './challenge-settings/challenge-settings.module';
import { ChallengeSponsorsModule } from './challenge-sponsors/challenge-sponsors.module';

@NgModule({
  imports: [
    CommonModule,
    MatTabsModule,
    UiModule,
    ChallengeRoutingModule,
    ChallengeHeaderModule,
    ChallengeStargazersModule,
    ChallengeSettingsModule,
    ChallengeSponsorsModule,
  ],
  declarations: [ChallengeComponent],
  exports: [ChallengeComponent],
})
export class ChallengeModule {}
