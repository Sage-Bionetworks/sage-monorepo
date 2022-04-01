import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChallengeComponent } from './challenge.component';
import { WebUiModule } from '@challenge-registry/web/ui';
import { ChallengeRoutingModule } from './challenge-routing.modules';
import { ChallengeHeaderModule } from './challenge-header/challenge-header.module';

@NgModule({
  imports: [
    CommonModule,
    WebUiModule,
    ChallengeRoutingModule,
    ChallengeHeaderModule,
  ],
  declarations: [ChallengeComponent],
  exports: [ChallengeComponent],
})
export class ChallengeModule {}
