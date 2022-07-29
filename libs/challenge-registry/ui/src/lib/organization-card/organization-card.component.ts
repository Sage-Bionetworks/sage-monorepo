import { Component, Input, OnInit } from '@angular/core';
import { Organization } from '@sagebionetworks/api-angular';
import { Avatar } from '../avatar/avatar';

@Component({
  selector: 'challenge-registry-organization-card',
  templateUrl: './organization-card.component.html',
  styleUrls: ['./organization-card.component.scss'],
})
export class OrganizationCardComponent implements OnInit {
  @Input() organization!: Organization;
  organizationAvatar!: Avatar;

  ngOnInit(): void {
    if (this.organization) {
      this.organizationAvatar = {
        name: this.organization.name
          ? (this.organization.name as string)
          : this.organization.login.replace(/-/g, ' '),
        src: this.organization.avatarUrl ? this.organization.avatarUrl : '',
        size: 188,
      };
    }
  }
}
