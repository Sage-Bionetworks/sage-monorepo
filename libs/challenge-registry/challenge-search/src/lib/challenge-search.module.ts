import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ChallengeRegistryUiModule } from '@sagebionetworks/challenge-registry/ui';
import { ChallengeSearchComponent } from './challenge-search.component';

const routes: Routes = [{ path: '', component: ChallengeSearchComponent }];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ChallengeRegistryUiModule,
  ],
  declarations: [ChallengeSearchComponent],
  exports: [ChallengeSearchComponent],
})
export class ChallengeSearchModule {}
