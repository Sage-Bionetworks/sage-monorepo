import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MemberCardComponent } from './member-card.component';
import { AvatarModule } from '../avatar/avatar.module';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [MemberCardComponent],
  imports: [CommonModule, AvatarModule, MatIconModule],
  exports: [MemberCardComponent],
})
export class MemberCardModule {}
