import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { OrgProfileStatsComponent } from './org-profile-stats.component';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [OrgProfileStatsComponent],
  imports: [CommonModule, MatIconModule, UiModule],
  exports: [OrgProfileStatsComponent],
})
export class OrgProfileStatsModule {}
