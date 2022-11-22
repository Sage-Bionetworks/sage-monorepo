import { Component, Input, OnInit } from '@angular/core';
import { Avatar } from '../avatar/avatar';

@Component({
  selector: 'challenge-registry-person-card',
  templateUrl: './person-card.component.html',
  styleUrls: ['./person-card.component.scss'],
})
export class PersonCardComponent implements OnInit {
  @Input() name!: string;
  @Input() username!: string;
  @Input() affiliation!: string | null | undefined;
  @Input() avatarUrl!: string | null | undefined;
  @Input() avatarSize = 120;
  @Input() role!: string | null | undefined;
  @Input() addChips = false;

  avatar!: Avatar;

  ngOnInit(): void {
    this.avatar = {
      name: this.name,
      src: this.avatarUrl ? this.avatarUrl : '',
      size: this.avatarSize,
    };
  }
}
