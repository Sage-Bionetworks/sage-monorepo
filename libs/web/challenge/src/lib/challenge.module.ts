import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChallengeComponent } from './challenge.component';
import { WebUiModule } from '@challenge-registry/web/ui';
import { ChallengeRoutingModule } from './challenge-routing.modules';

@NgModule({
  imports: [CommonModule, WebUiModule, ChallengeRoutingModule],
  declarations: [ChallengeComponent],
  exports: [ChallengeComponent],
})
export class ChallengeModule {}
