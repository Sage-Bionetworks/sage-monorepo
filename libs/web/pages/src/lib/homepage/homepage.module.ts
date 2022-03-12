import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { WebFeatureHomeModule } from '@challenge-registry/web/feature-home';
import { WebUiModule } from '@challenge-registry/web/ui';
import { HomepageComponent } from './homepage.component';

const routes: Routes = [{ path: '', component: HomepageComponent }];

@NgModule({
  declarations: [HomepageComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    WebFeatureHomeModule,
    WebUiModule,
  ],
  exports: [HomepageComponent],
})
export class HomepageModule {}
