import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ChallengeComponent } from './challenge.component';
import { WebUiModule } from '@challenge-registry/web/ui';

const routes: Routes = [{ path: '', component: ChallengeComponent }];

@NgModule({
  imports: [CommonModule, RouterModule.forChild(routes), WebUiModule],
  declarations: [ChallengeComponent],
  exports: [ChallengeComponent],
})
export class ChallengeModule {}
