import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { OrganizationMembersComponent } from './organization-members.component';

@NgModule({
  declarations: [OrganizationMembersComponent],
  imports: [CommonModule, UiModule],
  exports: [OrganizationMembersComponent],
})
export class OrganizationMembersModule {}
