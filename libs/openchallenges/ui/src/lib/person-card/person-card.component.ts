import { Component, Input, OnInit } from '@angular/core';
import { Avatar } from '../avatar/avatar';

import { MatIconModule } from '@angular/material/icon';
import { AvatarComponent } from '../avatar/avatar.component';

@Component({
  selector: 'openchallenges-person-card',
  imports: [AvatarComponent, MatIconModule],
  templateUrl: './person-card.component.html',
  styleUrls: ['./person-card.component.scss'],
})
export class PersonCardComponent implements OnInit {
  @Input({ required: true }) name!: string;
  @Input({ required: false }) username!: string;
  @Input({ required: true }) affiliation!: string | null | undefined;
  @Input({ required: false }) avatarUrl!: string | null | undefined;
  @Input({ required: true }) avatarSize = 120;
  @Input({ required: false }) role!: string | null | undefined;
  @Input({ required: false }) addChips = false;

  avatar!: Avatar;

  ngOnInit(): void {
    this.avatar = {
      name: this.name,
      src: this.avatarUrl ? this.avatarUrl : '',
      size: this.avatarSize,
    };
  }
}
