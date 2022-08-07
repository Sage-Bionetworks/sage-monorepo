import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AvatarModule as NgxAvatarModule } from 'ngx-avatars';
import { AvatarComponent } from './avatar.component';

@NgModule({
  declarations: [AvatarComponent],
  imports: [CommonModule, NgxAvatarModule],
  exports: [AvatarComponent],
})
export class AvatarModule {}
