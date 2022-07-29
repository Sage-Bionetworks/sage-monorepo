import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { OrganizationCardComponent } from './organization-card.component';
import { AvatarModule } from '../avatar/avatar.module';

@NgModule({
  declarations: [OrganizationCardComponent],
  imports: [CommonModule, MatCardModule, AvatarModule],
  exports: [OrganizationCardComponent],
})
export class OrganizationCardModule {}
