import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { OrgProfileComponent } from './org-profile.component';
import { OrgProfileHeaderModule } from './org-profile-header/org-profile-header.module';
import { OrgProfileOverviewModule } from './org-profile-overview/org-profile-overview.module';
import { OrgProfileRoutingModule } from './org-profile-routing.modules';

@NgModule({
  declarations: [OrgProfileComponent],
  imports: [
    CommonModule,
    MatTabsModule,
    UiModule,
    OrgProfileHeaderModule,
    OrgProfileOverviewModule,
    OrgProfileRoutingModule,
  ],
  exports: [OrgProfileComponent],
})
export class OrgProfileModule {}
