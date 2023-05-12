import { Component, Input, OnInit } from '@angular/core';
import { OrganizationCard } from './organization-card';
// import * as internal from 'stream';
import { Avatar } from '../avatar/avatar';

@Component({
  selector: 'openchallenges-organization-card',
  templateUrl: './organization-card.component.html',
  styleUrls: ['./organization-card.component.scss'],
})
export class OrganizationCardComponent implements OnInit {
  @Input() organizationCard!: OrganizationCard;
  // @Input() personAvatarSize = 36;
  organizationAvatar!: Avatar;
  avatar!: Avatar;
  mockStars!: number;
  mockMembers!: Avatar[];
  otherMembers!: number;
  challengesSupported: number | undefined;

  ngOnInit(): void {
    if (this.organizationCard) {
      this.organizationAvatar = {
        name: this.organizationCard.name,
        src: this.organizationCard.avatarUrl,
        size: 140,
      };
    }

    if (this.organizationCard.challengeCount) {
      this.challengesSupported =
        this.organizationCard.challengeCount > 0
          ? this.organizationCard.challengeCount
          : undefined;
    }

    // TODO: replace mock items with organization properties
    this.mockStars = 2;
    this.mockMembers = [
      // {
      //   name: 'Awesome User',
      //   src: '',
      //   size: this.personAvatarSize,
      // },
      // {
      //   name: 'Jane Doe',
      //   src: '',
      //   size: this.personAvatarSize,
      // },
      // {
      //   name: 'John Smith',
      //   src: '',
      //   size: this.personAvatarSize,
      // },
      // {
      //   name: 'Ash Ketchum',
      //   src: '',
      //   size: this.personAvatarSize,
      // },
      // {
      //   name: 'Misty',
      //   src: '',
      //   size: this.personAvatarSize,
      // },
      // {
      //   name: 'Brock',
      //   src: '',
      //   size: this.personAvatarSize,
      // },
    ];

    this.otherMembers =
      this.mockMembers.length > 4 ? this.mockMembers.length - 4 : 0;
  }
}
