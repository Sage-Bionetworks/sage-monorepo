import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { OrgProfileComponent } from './org-profile.component';
import { OrgProfileOverviewModule } from './org-profile-overview/org-profile-overview.module';
import { OrgProfileChallengesModule } from './org-profile-challenges/org-profile-challenges.module';
import { OrgProfileMembersModule } from './org-profile-members/org-profile-members.module';
import { OrgProfileRoutingModule } from './org-profile-routing.module';
import { OrgProfileStatsModule } from './org-profile-stats/org-profile-stats.module';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';

@NgModule({
  declarations: [OrgProfileComponent],
  imports: [
    CommonModule,
    MatTabsModule,
    MatIconModule,
    OrgProfileOverviewModule,
    OrgProfileChallengesModule,
    OrgProfileMembersModule,
    OrgProfileRoutingModule,
    OrgProfileStatsModule,
    UiModule,
  ],
  exports: [OrgProfileComponent],
})
export class OrgProfileModule {}
