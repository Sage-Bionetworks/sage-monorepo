import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/openchallenges/api-client-angular';
import {
  MOCK_ORGANIZATION_CARDS,
  OrganizationCard,
} from '@sagebionetworks/openchallenges/ui';

import { MatIconModule } from '@angular/material/icon';
import {
  IncentiveLabelPipe,
  SubmissionTypeLabelPipe,
} from '@sagebionetworks/openchallenges/util';
// import {
//   ChallengeIncentiveLabels,
//   ChallengeSubmissionTypeLabels,
// } from './challenge-property-labels';

@Component({
  selector: 'openchallenges-challenge-overview',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    IncentiveLabelPipe,
    SubmissionTypeLabelPipe,
  ],
  templateUrl: './challenge-overview.component.html',
  styleUrls: ['./challenge-overview.component.scss'],
})
export class ChallengeOverviewComponent {
  @Input({ required: true }) challenge!: Challenge;
  organizationCards: OrganizationCard[] = MOCK_ORGANIZATION_CARDS;
  // incentiveLabels = ChallengeIncentiveLabels;
  // submissionTypeLabels = ChallengeSubmissionTypeLabels;

  useNaIfFalsey(str: string | null | undefined) {
    return str ?? 'Not available';
  }
}
