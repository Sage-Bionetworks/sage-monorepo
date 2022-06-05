import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrgProfileHeaderComponent } from './org-profile-header.component';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [OrgProfileHeaderComponent],
  imports: [CommonModule, MatIconModule],
  exports: [OrgProfileHeaderComponent],
})
export class OrgProfileHeaderModule {}
