import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { TeamComponent } from './team.component';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [{ path: '', component: TeamComponent }];

@NgModule({
  imports: [CommonModule, RouterModule.forChild(routes), UiModule],
  declarations: [TeamComponent],
  exports: [TeamComponent],
})
export class TeamModule {}
