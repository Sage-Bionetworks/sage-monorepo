import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PersonCardComponent } from './person-card.component';
import { MatIconModule } from '@angular/material/icon';
import { AvatarComponent } from '../avatar/avatar.component';

@NgModule({
  declarations: [PersonCardComponent],
  imports: [CommonModule, AvatarComponent, MatIconModule],
  exports: [PersonCardComponent],
})
export class PersonCardModule {}
