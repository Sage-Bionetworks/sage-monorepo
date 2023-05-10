import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacyMenuModule as MatMenuModule } from '@angular/material/legacy-menu';
import { UserButtonComponent } from './user-button.component';
import { AvatarComponent } from '../avatar/avatar.component';

@NgModule({
  declarations: [UserButtonComponent],
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    MatMenuModule,
    AvatarComponent,
  ],
  exports: [UserButtonComponent],
})
export class UserButtonModule {}
