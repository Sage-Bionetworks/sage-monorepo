import { Component, Input, OnInit } from '@angular/core';
import { Organization } from '@challenge-registry/api-angular';
import { Avatar } from '../avatar/avatar';

@Component({
  selector: 'challenge-registry-org-card',
  templateUrl: './org-card.component.html',
  styleUrls: ['./org-card.component.scss'],
})
export class OrgCardComponent implements OnInit {
  @Input() org!: Organization;
  @Input() orgAvatar!: Avatar;

  ngOnInit(): void {
    if (this.orgAvatar) {
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
