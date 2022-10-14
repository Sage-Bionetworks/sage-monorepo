import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { OrganizationStatsComponent } from './organization-stats.component';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [OrganizationStatsComponent],
  imports: [CommonModule, MatIconModule, UiModule],
  exports: [OrganizationStatsComponent],
})
export class OrganizationStatsModule {}
