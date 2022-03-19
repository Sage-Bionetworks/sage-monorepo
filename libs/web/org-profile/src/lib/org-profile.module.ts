import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { OrgProfileComponent } from './org-profile.component';
import { WebUiModule } from '@challenge-registry/web/ui';
import { OrgProfileHeaderModule } from './org-profile-header/org-profile-header.module';

const routes: Routes = [{ path: '', component: OrgProfileComponent }];

@NgModule({
  declarations: [OrgProfileComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    WebUiModule,
    OrgProfileHeaderModule,
  ],
  exports: [OrgProfileComponent],
})
export class OrgProfileModule {}
