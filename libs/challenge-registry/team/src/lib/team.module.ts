import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { TeamComponent } from './team.component';

@NgModule({
  imports: [CommonModule, UiModule],
  declarations: [TeamComponent],
  exports: [TeamComponent],
})
export class TeamModule {}
