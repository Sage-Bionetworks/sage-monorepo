import { Component, Input, OnInit } from '@angular/core';
import { Organization } from '@sagebionetworks/openchallenges/api-client-angular';
import { Avatar } from '../avatar/avatar';

@Component({
  selector: 'openchallenges-organization-card',
  templateUrl: './organization-card.component.html',
  styleUrls: ['./organization-card.component.scss'],
})
export class OrganizationCardComponent implements OnInit {
  @Input() organization!: Organization;
  organizationAvatar!: Avatar;

  ngOnInit(): void {
    if (this.organization) {
      this.organizationAvatar = {
        name: this.organization.name || this.organization.login,
        src: this.organization.avatarUrl || '',
        size: 120,
      };
    }
  }
}
