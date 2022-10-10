import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { OrganizationComponent } from './organization.component';
import { UiModule } from '@sagebionetworks/challenge-registry/ui';

@NgModule({
  declarations: [OrganizationComponent],
  imports: [CommonModule, MatTabsModule, MatIconModule, UiModule],
  exports: [OrganizationComponent],
})
export class OrganizationModule {}
