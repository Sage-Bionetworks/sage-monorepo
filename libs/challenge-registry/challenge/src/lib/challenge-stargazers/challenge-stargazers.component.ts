import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';
import {
  MOCK_PERSONS,
  MOCK_ORGANIZATIONS,
} from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-challenge-stargazers',
  templateUrl: './challenge-stargazers.component.html',
  styleUrls: ['./challenge-stargazers.component.scss'],
})
export class ChallengeStargazersComponent {
  @Input() challenge!: Challenge;
  stargazers = MOCK_PERSONS;
  organization = MOCK_ORGANIZATIONS[0];
}
