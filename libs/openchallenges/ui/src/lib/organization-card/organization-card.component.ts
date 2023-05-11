import { Component, Input, OnInit } from '@angular/core';
import { OrganizationCardData } from './organization-card-data';
// import * as internal from 'stream';
import { Avatar } from '../avatar/avatar';

@Component({
  selector: 'openchallenges-organization-card',
  templateUrl: './organization-card.component.html',
  styleUrls: ['./organization-card.component.scss'],
})
export class OrganizationCardComponent implements OnInit {
  @Input() organizationCardData!: OrganizationCardData;
  // @Input() personAvatarSize = 36;
  organizationAvatar!: Avatar;
  avatar!: Avatar;
  mockStars!: number;
  mockMembers!: Avatar[];
  otherMembers!: number;
  challengesSupported: number | undefined;

  ngOnInit(): void {
    if (this.organizationCardData) {
      this.organizationAvatar = {
        value: this.organizationCardData.acronym,
        name: this.organizationCardData.name,
        src: this.organizationCardData.avatarUrl || '',
        size: 140,
      };
    }

    if (this.organizationCardData.challengeCount) {
      this.challengesSupported =
        this.organizationCardData.challengeCount > 0
          ? this.organizationCardData.challengeCount
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
