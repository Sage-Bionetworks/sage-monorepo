import { Component, Input } from '@angular/core';
import { AvatarModule as NgxAvatarModule } from 'ngx-avatars';
import { Avatar } from './avatar';

@Component({
  selector: 'openchallenges-avatar',
  imports: [NgxAvatarModule],
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss'],
})
export class AvatarComponent {
  @Input({ required: false }) name = '';
  @Input({ required: false }) src = '';
  @Input({ required: false }) size = 32;
  @Input({ required: false }) value = '';
  // ngx-avatars uses this value internally when generating the initials from `name`
  initialsSize = 1;

  @Input({ required: false }) set avatar(avatar: Avatar) {
    if (avatar) {
      // Here we change the default ngx-avatars priority from name > value to value > name
      if (avatar.value) {
        this.value =
          avatar.value.length > this.initialsSize
            ? avatar.value.substring(0, this.initialsSize)
            : avatar.value;
      } else if (avatar.name) {
        this.name = avatar.name;
      }
      this.src = avatar.src || '';
      this.size = avatar.size;
    }
  }

  get avatar(): Avatar {
    return {
      name: this.name,
      src: this.src,
      size: this.size,
      value: this.value,
    };
  }
}
