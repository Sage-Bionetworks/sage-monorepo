import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/api-client-angular-deprecated';
import { MOCK_ORG_MEMBERS } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-challenge-stargazers',
  templateUrl: './challenge-stargazers.component.html',
  styleUrls: ['./challenge-stargazers.component.scss'],
})
export class ChallengeStargazersComponent {
  @Input() challenge!: Challenge;
  members = MOCK_ORG_MEMBERS;
}
