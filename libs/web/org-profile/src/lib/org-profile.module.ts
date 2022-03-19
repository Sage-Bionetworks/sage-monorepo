import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { WebUiModule } from '@challenge-registry/web/ui';
import { OrgProfileComponent } from './org-profile.component';
import { OrgProfileHeaderModule } from './org-profile-header/org-profile-header.module';
import { OrgProfileOverviewModule } from './org-profile-overview/org-profile-overview.module';

const routes: Routes = [{ path: '', component: OrgProfileComponent }];

@NgModule({
  declarations: [OrgProfileComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatTabsModule,
    WebUiModule,
    OrgProfileHeaderModule,
    OrgProfileOverviewModule,
  ],
  exports: [OrgProfileComponent],
})
export class OrgProfileModule {}
