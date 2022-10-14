import { Component, Input } from '@angular/core';
import {
  Challenge,
  Organization,
  UserService,
} from '@sagebionetworks/api-client-angular-deprecated';
import { MOCK_CHALLENGES } from '@sagebionetworks/challenge-registry/ui';
// import { map, Observable } from 'rxjs';

@Component({
  selector: 'challenge-registry-organization-challenges',
  templateUrl: './organization-challenges.component.html',
  styleUrls: ['./organization-challenges.component.scss'],
})
export class OrganizationChallengesComponent {
  @Input() organization!: Organization;
  challenges: Challenge[] = MOCK_CHALLENGES;

  constructor(private userService: UserService) {}
}
