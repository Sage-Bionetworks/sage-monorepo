import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrganizationCardComponent } from './organization-card.component';
import { AvatarModule } from '../avatar/avatar.module';

@NgModule({
  declarations: [OrganizationCardComponent],
  imports: [CommonModule, AvatarModule],
  exports: [OrganizationCardComponent],
})
export class OrganizationCardModule {}
