import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PersonCardComponent } from './person-card.component';
import { AvatarModule } from '../avatar/avatar.module';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [PersonCardComponent],
  imports: [CommonModule, AvatarModule, MatIconModule],
  exports: [PersonCardComponent],
})
export class PersonCardModule {}
