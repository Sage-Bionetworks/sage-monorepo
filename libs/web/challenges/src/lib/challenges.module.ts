import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ChallengesComponent } from './challenges.component';
import { WebUiModule } from '@challenge-registry/web/ui';

const routes: Routes = [{ path: '', component: ChallengesComponent }];

@NgModule({
  imports: [CommonModule, RouterModule.forChild(routes), WebUiModule],
  declarations: [ChallengesComponent],
  exports: [ChallengesComponent],
})
export class ChallengesModule {}
