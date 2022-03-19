import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { WebUiModule } from '@challenge-registry/web/ui';
import { OrgProfileComponent } from './org-profile.component';
import { OrgProfileHeaderModule } from './org-profile-header/org-profile-header.module';
import { OrgProfileOverviewModule } from './org-profile-overview/org-profile-overview.module';
import { OrgProfileRoutingModule } from './org-profile-routing.modules';

@NgModule({
  declarations: [OrgProfileComponent],
  imports: [
    CommonModule,
    MatTabsModule,
    WebUiModule,
    OrgProfileRoutingModule,
    OrgProfileHeaderModule,
    OrgProfileOverviewModule,
  ],
  exports: [OrgProfileComponent],
})
export class OrgProfileModule {}
