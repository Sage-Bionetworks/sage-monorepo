import { Component, Input, OnInit } from '@angular/core';
import { Organization } from './organization';
// import * as internal from 'stream';
import { Avatar } from '../avatar/avatar';

@Component({
  selector: 'openchallenges-organization-card',
  templateUrl: './organization-card.component.html',
  styleUrls: ['./organization-card.component.scss'],
})
export class OrganizationCardComponent implements OnInit {
  @Input() organization!: Organization;
  // @Input() personAvatarSize = 36;
  organizationAvatar!: Avatar;
  avatar!: Avatar;
  mockStars!: number;
  mockMembers!: Avatar[];
  otherMembers!: number;
  challengesSupported: number | undefined;

  ngOnInit(): void {
    if (this.organization) {
      this.organizationAvatar = {
        value: this.organization.acronym,
        name: this.organization.name,
        src: this.organization.avatarUrl || '',
        size: 140,
      };
    }

    if (this.organization.challengeCount) {
      this.challengesSupported =
        this.organization.challengeCount > 0
          ? this.organization.challengeCount
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
