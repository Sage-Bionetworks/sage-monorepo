import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { UserButtonComponent } from './user-button.component';
import { AvatarModule } from '../avatar/avatar.module';

@NgModule({
  declarations: [UserButtonComponent],
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    MatMenuModule,
    AvatarModule,
  ],
  exports: [UserButtonComponent],
})
export class UserButtonModule {}
