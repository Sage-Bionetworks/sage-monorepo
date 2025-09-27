import { NgClass } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/openchallenges/api-client';
import { MOCK_ORGANIZATION_CARDS, OrganizationCard } from '@sagebionetworks/openchallenges/ui';

import { MatIconModule } from '@angular/material/icon';
import {
  ChallengeIncentiveLabelPipe,
  ChallengeStatusLabelPipe,
  ChallengeSubmissionTypeLabelPipe,
} from '@sagebionetworks/openchallenges/util';
@Component({
  selector: 'openchallenges-challenge-overview',
  imports: [
    ChallengeIncentiveLabelPipe,
    ChallengeStatusLabelPipe,
    ChallengeSubmissionTypeLabelPipe,
    NgClass,
    MatIconModule,
  ],
  templateUrl: './challenge-overview.component.html',
  styleUrls: ['./challenge-overview.component.scss'],
})
export class ChallengeOverviewComponent {
  @Input({ required: true }) challenge!: Challenge;
  organizationCards: OrganizationCard[] = MOCK_ORGANIZATION_CARDS;

  useNaIfFalsey(str: string | null | undefined) {
    return str ?? 'Not available';
  }
}
