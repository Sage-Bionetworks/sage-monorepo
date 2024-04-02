import { Pipe, PipeTransform } from '@angular/core';
import {
  ChallengeIncentive,
  ChallengeSubmissionType,
} from '@sagebionetworks/openchallenges/api-client-angular';
import {
  challengeIncentivesLabels,
  challengeSubmissionTypesLabels,
} from './challenge-property-labels';

@Pipe({
  name: 'incentiveLabel',
  standalone: true,
})
export class IncentiveLabelPipe implements PipeTransform {
  transform(incentive: ChallengeIncentive): string | undefined {
    const filterItem = challengeIncentivesLabels.find(
      (item) => item.value === incentive
    );
    return filterItem ? filterItem.label : undefined;
  }
}

@Pipe({
  name: 'submissionTypeLabel',
  standalone: true,
})
export class SubmissionTypeLabelPipe implements PipeTransform {
  transform(submissionType: ChallengeSubmissionType): string | undefined {
    const filterItem = challengeSubmissionTypesLabels.find(
      (item) => item.value === submissionType
    );
    return filterItem ? filterItem.label : undefined;
  }
}
