import { Pipe, PipeTransform } from '@angular/core';
import {
  ChallengeIncentive,
  ChallengeSubmissionType,
} from '@sagebionetworks/openchallenges/api-client-angular';
// Directly import challenge filters from the challenge-search module to avoid circular dependencies.
// Do not import these from a index file that might cause circular import paths.
import {
  challengeIncentivesFilter,
  challengeSubmissionTypesFilter,
} from '../../../../challenge-search/src/lib/challenge-search-filters';

@Pipe({
  name: 'incentiveLabel',
  standalone: true,
})
export class IncentiveLabelPipe implements PipeTransform {
  transform(incentive: ChallengeIncentive): string | undefined {
    const filterItem = challengeIncentivesFilter.find(
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
    const filterItem = challengeSubmissionTypesFilter.find(
      (item) => item.value === submissionType
    );
    return filterItem ? filterItem.label : undefined;
  }
}
