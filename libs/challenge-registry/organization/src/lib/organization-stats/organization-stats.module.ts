import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { OrganizationStatsComponent } from './organization-stats.component';

@NgModule({
  declarations: [OrganizationStatsComponent],
  imports: [CommonModule, UiModule],
  exports: [OrganizationStatsComponent],
})
export class OrganizationStatsModule {}
