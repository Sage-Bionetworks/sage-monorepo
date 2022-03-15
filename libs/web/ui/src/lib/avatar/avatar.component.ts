import { Component, Input } from '@angular/core';
import { Avatar } from './avatar';

@Component({
  selector: 'challenge-registry-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss'],
})
export class AvatarComponent {
  @Input() name = '';
  @Input() src = '';
  @Input() size = 32;

  @Input() set avatar(avatar: Avatar) {
    if (avatar) {
      this.name = avatar.name;
      this.src = avatar.src;
      this.size = avatar.size;
    }
  }

  get avatar(): Avatar {
    return {
      name: this.name,
      src: this.src,
      size: this.size,
    };
  }
}
