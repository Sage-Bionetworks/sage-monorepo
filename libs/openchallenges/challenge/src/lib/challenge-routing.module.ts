import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChallengeComponent } from './challenge.component';
import { ChallengeOverviewModule } from './challenge-overview/challenge-overview.module';

const routes: Routes = [
  {
    path: ':challengeName',
    component: ChallengeComponent,
    children: [{ path: ':challengeName', component: ChallengeOverviewModule }],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ChallengeRoutingModule {}
