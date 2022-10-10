import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { OrganizationOverviewComponent } from './organization-overview.component';

@NgModule({
  declarations: [OrganizationOverviewComponent],
  imports: [CommonModule, UiModule],
  exports: [OrganizationOverviewComponent],
})
export class OrganizationOverviewModule {}
