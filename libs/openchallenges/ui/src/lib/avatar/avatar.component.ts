import { Component, Input } from '@angular/core';
import { AvatarModule as NgxAvatarModule } from 'ngx-avatars';
import { Avatar } from './avatar';

@Component({
  selector: 'openchallenges-avatar',
  standalone: true,
  imports: [NgxAvatarModule],
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss'],
})
export class AvatarComponent {
  @Input() name = '';
  @Input() src = '';
  @Input() size = 32;
  @Input() value = '';
  // ngx-avatars uses this value internally when generating the initials from `name`
  initialsSize = 2;

  @Input() set avatar(avatar: Avatar) {
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
      // this.src = avatar.src || '';
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
