import { Component } from '@angular/core';
import { Challenge } from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
import { MOCK_CHALLENGES } from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-featured-challenge-list',
  templateUrl: './featured-challenge-list.component.html',
  styleUrls: ['./featured-challenge-list.component.scss'],
})
export class FeaturedChallengeListComponent {
  challenges: Challenge[] = MOCK_CHALLENGES;
}
