import { Component, Input } from '@angular/core';
import {
  Challenge,
  UserService,
} from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';
import { Organization } from '@sagebionetworks/challenge-registry/api-client-angular';
import { MOCK_CHALLENGES } from '@sagebionetworks/challenge-registry/ui';
// import { map, Observable } from 'rxjs';

@Component({
  selector: 'challenge-registry-org-profile-challenges',
  templateUrl: './org-profile-challenges.component.html',
  styleUrls: ['./org-profile-challenges.component.scss'],
})
export class OrgProfileChallengesComponent {
  @Input() organization!: Organization;
  challenges: Challenge[] = MOCK_CHALLENGES;

  constructor(private userService: UserService) {}
}
