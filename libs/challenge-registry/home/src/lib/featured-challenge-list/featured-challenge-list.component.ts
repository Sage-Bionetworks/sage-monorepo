import { Component } from '@angular/core';
import {
  Challenge
} from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';
import { MOCK_CHALLENGES } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-featured-challenge-list',
  templateUrl: './featured-challenge-list.component.html',
  styleUrls: ['./featured-challenge-list.component.scss'],
})
export class FeaturedChallengeListComponent {
  challenges: Challenge[] = MOCK_CHALLENGES;
}
