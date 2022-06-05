import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AboutComponent } from './about.component';
import { RouterModule, Routes } from '@angular/router';
import { ChallengeRegistryUiModule } from '@sagebionetworks/challenge-registry/ui';

const routes: Routes = [{ path: '', component: AboutComponent }];

@NgModule({
  declarations: [AboutComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ChallengeRegistryUiModule,
  ],
  exports: [AboutComponent],
})
export class AboutModule {}
