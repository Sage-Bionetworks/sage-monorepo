import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import {
  Challenge,
  ChallengeIncentive,
  ChallengeStatus,
  ChallengeSubmissionType,
} from '@sagebionetworks/openchallenges/api-client-angular';
import {
  MOCK_ORGANIZATION_CARDS,
  OrganizationCard,
} from '@sagebionetworks/openchallenges/ui';
import {
  challengeIncentivesFilter,
  challengeStatusFilter,
  challengeSubmissionTypesFilter,
  getLabelByFilterValue,
} from '@sagebionetworks/openchallenges/challenge-search';

import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'openchallenges-challenge-overview',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './challenge-overview.component.html',
  styleUrls: ['./challenge-overview.component.scss'],
})
export class ChallengeOverviewComponent {
  @Input({ required: true }) challenge!: Challenge;
  organizationCards: OrganizationCard[] = MOCK_ORGANIZATION_CARDS;

  useNaIfFalsey(str: string | null | undefined) {
    return str ?? 'Not available';
  }

  getIncentiveLabel(status: ChallengeIncentive): string | undefined {
    return getLabelByFilterValue(challengeIncentivesFilter, status);
  }

  getSubmissionTypeLabel(status: ChallengeSubmissionType): string | undefined {
    return getLabelByFilterValue(challengeSubmissionTypesFilter, status);
  }

  getStatusLabel(status: ChallengeStatus): string | undefined {
    return getLabelByFilterValue(challengeStatusFilter, status);
  }
}
