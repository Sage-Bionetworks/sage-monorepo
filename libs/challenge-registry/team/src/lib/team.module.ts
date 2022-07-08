import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TeamComponent } from './team.component';

@NgModule({
  imports: [CommonModule],
  declarations: [TeamComponent],
  exports: [TeamComponent],
})
export class ChallengeRegistryTeamModule {}
