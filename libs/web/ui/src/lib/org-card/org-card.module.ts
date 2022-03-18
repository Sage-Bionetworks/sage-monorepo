import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { OrgCardComponent } from './org-card.component';
import { AvatarModule } from '../avatar/avatar.module';

@NgModule({
  declarations: [OrgCardComponent],
  imports: [CommonModule, MatCardModule, AvatarModule],
  exports: [OrgCardComponent],
})
export class OrgCardModule {}
