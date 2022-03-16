import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserProfileBarModule } from './user-profile-bar/user-profile-bar.module';

@NgModule({
  imports: [CommonModule],
  declarations: [],
  exports: [UserProfileBarModule],
})
export class WebFeatureUserModule {}
