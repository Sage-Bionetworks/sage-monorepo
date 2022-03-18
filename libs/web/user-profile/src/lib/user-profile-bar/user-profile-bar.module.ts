import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { WebUiModule } from '@challenge-registry/web/ui';
import { UserProfileBarComponent } from './user-profile-bar.component';
import { MatButtonModule } from '@angular/material/button';

@NgModule({
  declarations: [UserProfileBarComponent],
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    WebUiModule,
  ],
  exports: [UserProfileBarComponent],
})
export class UserProfileBarModule {}
