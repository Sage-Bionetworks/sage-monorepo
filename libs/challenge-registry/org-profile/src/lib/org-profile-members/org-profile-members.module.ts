import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';
import { OrgProfileMembersComponent } from './org-profile-members.component';

@NgModule({
  declarations: [OrgProfileMembersComponent],
  imports: [CommonModule, UiModule],
  exports: [OrgProfileMembersComponent],
})
export class OrgProfileMembersModule {}