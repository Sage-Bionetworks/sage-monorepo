import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/openchallenges/ui';
import { OrgProfileOverviewComponent } from './org-profile-overview.component';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [OrgProfileOverviewComponent],
  imports: [CommonModule, MatIconModule, UiModule],
  exports: [OrgProfileOverviewComponent],
})
export class OrgProfileOverviewModule {}
