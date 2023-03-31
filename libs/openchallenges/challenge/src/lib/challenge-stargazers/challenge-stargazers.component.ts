import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/openchallenges/api-client-angular';
import {
  MOCK_PERSONS,
  MOCK_ORGANIZATIONS,
} from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-challenge-stargazers',
  templateUrl: './challenge-stargazers.component.html',
  styleUrls: ['./challenge-stargazers.component.scss'],
})
export class ChallengeStargazersComponent {
  @Input() challenge!: Challenge;
  stargazers = MOCK_PERSONS;
  organization = MOCK_ORGANIZATIONS[0];
}
