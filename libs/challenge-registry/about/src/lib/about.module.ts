import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AboutComponent } from './about.component';
import { RouterModule, Routes } from '@angular/router';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { SharedUtilModule } from '@sagebionetworks/shared/util';

const routes: Routes = [{ path: '', component: AboutComponent }];

@NgModule({
  declarations: [AboutComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    UiModule,
    SharedUtilModule,
  ],
  exports: [AboutComponent],
})
export class AboutModule {}
