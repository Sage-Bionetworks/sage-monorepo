import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrganizationCardComponent } from './organization-card.component';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { AvatarComponent } from '../avatar/avatar.component';

@NgModule({
  declarations: [OrganizationCardComponent],
  imports: [CommonModule, AvatarComponent, MatIconModule, RouterModule],
  exports: [OrganizationCardComponent],
})
export class OrganizationCardModule {}
