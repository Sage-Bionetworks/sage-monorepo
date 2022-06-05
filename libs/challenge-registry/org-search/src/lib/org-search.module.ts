import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ChallengeRegistryUiModule } from '@sagebionetworks/challenge-registry/ui';
import { OrgSearchComponent } from './org-search.component';

const routes: Routes = [{ path: '', component: OrgSearchComponent }];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ChallengeRegistryUiModule,
  ],
  declarations: [OrgSearchComponent],
  exports: [OrgSearchComponent],
})
export class OrgSearchModule {}
