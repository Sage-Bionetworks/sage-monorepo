import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserProfileBarComponent } from './user-profile-bar.component';

@NgModule({
  declarations: [UserProfileBarComponent],
  imports: [CommonModule],
  exports: [UserProfileBarComponent],
})
export class UserProfileBarModule {}
