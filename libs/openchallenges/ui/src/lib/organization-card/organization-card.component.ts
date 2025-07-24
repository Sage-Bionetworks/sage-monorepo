import { Component, Input, OnInit } from '@angular/core';
import { OrganizationCard } from './organization-card';
import { Avatar } from '../avatar/avatar';
import { MOCK_MEMBERS, OrganizationMember } from './mock-members';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { AvatarComponent } from '../avatar/avatar.component';
import { SlicePipe } from '@angular/common';

@Component({
  selector: 'openchallenges-organization-card',
  imports: [AvatarComponent, MatIconModule, RouterModule, SlicePipe],
  templateUrl: './organization-card.component.html',
  styleUrls: ['./organization-card.component.scss'],
})
export class OrganizationCardComponent implements OnInit {
  @Input({ required: true }) organizationCard!: OrganizationCard;
  @Input({ required: false }) showMember = false;
  @Input({ required: false }) stars = 0;
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
        size: 160,
      };
    }

    if (this.organizationCard.challengeCount) {
      this.challengesSupported =
        this.organizationCard.challengeCount > 0 ? this.organizationCard.challengeCount : undefined;
    }

    // TODO: retrieve stars from org object
    this.stars = this.mockStars;
    // TODO: retrieve members from org object
    this.organizationMembers = MOCK_MEMBERS;
    this.otherMembersCount = Math.max(this.organizationMembers.length - 4, 0);
  }
}
