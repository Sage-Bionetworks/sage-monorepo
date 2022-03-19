import { Component, Input, OnInit } from '@angular/core';
import {
  ChallengeService,
  Organization,
  OrgMembershipService,
} from '@challenge-registry/api-angular';
import { map, Observable } from 'rxjs';
import { map as _map, uniqBy as _uniqBy } from 'lodash';

@Component({
  selector: 'challenge-registry-org-profile-header',
  templateUrl: './org-profile-header.component.html',
  styleUrls: ['./org-profile-header.component.scss'],
})
export class OrgProfileHeaderComponent implements OnInit {
  @Input() org!: Organization;
  numChallenges$!: Observable<number>;
  numPeople$!: Observable<number>;

  constructor(
    private challengeService: ChallengeService,
    private orgMembershipService: OrgMembershipService
  ) {}

  ngOnInit(): void {
    this.numChallenges$ = this.challengeService
      .listAccountChallenges(this.org.login, 50, 0)
      .pipe(map((page) => page.totalResults));

    this.numPeople$ = this.orgMembershipService
      .listOrgMemberships(50, 0, this.org.id, undefined)
      .pipe(
        map((page) => page.orgMemberships),
        map((orgMemberships) =>
          _map(_uniqBy(orgMemberships, 'userId'), 'userId')
        ),
        map((userIds) => (userIds === undefined ? 0 : userIds.length))
      );
  }
}
