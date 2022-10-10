import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { OrganizationComponent } from './organization.component';
import { OrganizationOverviewModule } from './organization-overview/organization-overview.module';
import { OrganizationChallengesModule } from './organization-challenges/organization-challenges.module';
import { OrganizationMembersModule } from './organization-members/organization-members.module';
import { OrganizationStatsModule } from './organization-stats/organization-stats';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';

@NgModule({
  declarations: [OrganizationComponent],
  imports: [
    CommonModule,
    MatTabsModule,
    MatIconModule,
    OrganizationOverviewModule,
    OrganizationChallengesModule,
    OrganizationMembersModule,
    OrganizationStatsModule,
    UiModule,
  ],
  exports: [OrganizationComponent],
})
export class OrganizationModule {}
