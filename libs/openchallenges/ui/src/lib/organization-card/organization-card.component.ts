import { Component, Input, OnInit } from '@angular/core';
import { Organization } from '@sagebionetworks/openchallenges/api-client-angular';
// import * as internal from 'stream';
import { Avatar } from '../avatar/avatar';

@Component({
  selector: 'openchallenges-organization-card',
  templateUrl: './organization-card.component.html',
  styleUrls: ['./organization-card.component.scss'],
})
export class OrganizationCardComponent implements OnInit {
  @Input() organization!: Organization;
  @Input() personAvatarSize = 36;
  organizationAvatar!: Avatar;
  avatar!: Avatar;
  mockStars!: number;
  mockMembers!: Avatar[];
  otherMembers!: number;
  mockChallengesSupported!: number;

  ngOnInit(): void {
    if (this.organization) {
      this.organizationAvatar = {
        name:
          this.organization.name || this.organization.login.replace(/-/g, ' '),
        src: this.organization.avatarUrl || '',
        size: 140,
      };
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
    this.mockChallengesSupported = 3;

    this.otherMembers =
      this.mockMembers.length > 4 ? this.mockMembers.length - 4 : 0;
  }
}
