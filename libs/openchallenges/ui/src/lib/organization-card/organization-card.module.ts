import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrganizationCardComponent } from './organization-card.component';
import { AvatarModule } from '../avatar/avatar.module';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [OrganizationCardComponent],
  imports: [CommonModule, AvatarModule, MatIconModule, RouterModule],
  exports: [OrganizationCardComponent],
})
export class OrganizationCardModule {}
