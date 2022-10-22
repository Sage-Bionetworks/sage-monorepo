import { Component, Input, OnInit } from '@angular/core';
import { Avatar } from '../avatar/avatar';
import { Organization } from '@sagebionetworks/api-client-angular-deprecated';
import { MOCK_ORGANIZATIONS } from '../organization-card/mock-organizations';
import { OrgMember } from './members';

@Component({
  selector: 'challenge-registry-member-card',
  templateUrl: './member-card.component.html',
  styleUrls: ['./member-card.component.scss'],
})
export class MemberCardComponent implements OnInit {
  @Input() size = 120;
  @Input() member!: OrgMember;

  memberAvatar!: Avatar;
  isAdmin!: boolean;
  // TODO: replace by querying organziation with organizationId
  memberOrg: Organization = MOCK_ORGANIZATIONS[0];

  ngOnInit(): void {
    if (this.member) {
      this.memberAvatar = {
        name: this.member.name,
        src: this.member.avatarUrl ? this.member.avatarUrl : '',
        size: this.size,
      };

      const memberRoles = this.member.roles ? this.member.roles : [''];
      this.isAdmin = memberRoles.includes('admin');
    }
  }
}
