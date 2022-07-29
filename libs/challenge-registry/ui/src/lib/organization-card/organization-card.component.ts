import { Component, Input, OnInit } from '@angular/core';
import { Organization } from '@sagebionetworks/api-angular';
import { Avatar } from '../avatar/avatar';

@Component({
  selector: 'challenge-registry-organization-card',
  templateUrl: './organization-card.component.html',
  styleUrls: ['./organization-card.component.scss'],
})
export class OrganizationCardComponent implements OnInit {
  @Input() org!: Organization;
  orgAvatar!: Avatar;

  ngOnInit(): void {
    if (this.org) {
      this.orgAvatar = {
        name: this.org.name
          ? (this.org.name as string)
          : this.org.login.replace(/-/g, ' '),
        src: this.org.avatarUrl ? this.org.avatarUrl : '',
        size: 100,
      };
    }
  }
}
