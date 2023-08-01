import { Component, Input, OnInit } from '@angular/core';
import { OrganizationCard } from './organization-card';
import { Avatar } from '../avatar/avatar';
import { MOCK_MEMBERS, OrganizationMember } from './mock-members';

@Component({
  selector: 'openchallenges-organization-card',
  templateUrl: './organization-card.component.html',
  styleUrls: ['./organization-card.component.scss'],
})
export class OrganizationCardComponent implements OnInit {
  @Input() organizationCard!: OrganizationCard;
  @Input() showMember = true;
  @Input() stars = 0;
  organizationAvatar!: Avatar;
  organizationMembers!: OrganizationMember[];

  mockStars = 2;
  otherMembersCount!: number;
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

    // TODO: retrieve stars from org object
    this.stars = this.mockStars;
    // TODO: retrieve members from org object
    this.organizationMembers = MOCK_MEMBERS;
    this.otherMembersCount = Math.max(this.organizationMembers.length - 4, 0);
  }
}
