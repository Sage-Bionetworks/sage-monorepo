import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FooterComponent, UiModule } from '@sagebionetworks/openchallenges/ui';
import { TeamComponent } from './team.component';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [{ path: '', component: TeamComponent }];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    UiModule,
    FooterComponent,
  ],
  declarations: [TeamComponent],
  exports: [TeamComponent],
})
export class TeamModule {}
