import { Pipe, PipeTransform } from '@angular/core';
import {
  ChallengeCategory,
  ChallengeIncentive,
  ChallengeSort,
  ChallengeStatus,
  ChallengeSubmissionType,
} from '@sagebionetworks/openchallenges/api-client';
import {
  ChallengeCategoriesOptions,
  ChallengeIncentivesOptions,
  ChallengeSortOptions,
  ChallengeStatusOptions,
  ChallengeSubmissionTypesOptions,
} from './challenge-property-options';

@Pipe({
  name: 'challengeCategoryLabel',
  standalone: true,
})
export class ChallengeCategoryLabelPipe implements PipeTransform {
  transform(category: ChallengeCategory): string | undefined {
    const option = ChallengeCategoriesOptions.find((item) => item.value === category);
    return option ? option.label : undefined;
  }
}

@Pipe({
  name: 'challengeIncentiveLabel',
  standalone: true,
})
export class ChallengeIncentiveLabelPipe implements PipeTransform {
  transform(incentive: ChallengeIncentive): string | undefined {
    const option = ChallengeIncentivesOptions.find((item) => item.value === incentive);
    return option ? option.label : undefined;
  }
}

@Pipe({
  name: 'challengeSortLabel',
  standalone: true,
})
export class ChallengeSortLabelPipe implements PipeTransform {
  transform(sort: ChallengeSort): string | undefined {
    const option = ChallengeSortOptions.find((item) => item.value === sort);
    return option ? option.label : undefined;
  }
}

@Pipe({
  name: 'challengeStatusLabel',
  standalone: true,
})
export class ChallengeStatusLabelPipe implements PipeTransform {
  transform(status: ChallengeStatus): string | undefined {
    const option = ChallengeStatusOptions.find((item) => item.value === status);
    return option ? option.label : undefined;
  }
}

@Pipe({
  name: 'challengeSubmissionTypeLabel',
  standalone: true,
})
export class ChallengeSubmissionTypeLabelPipe implements PipeTransform {
  transform(submissionType: ChallengeSubmissionType): string | undefined {
    const option = ChallengeSubmissionTypesOptions.find((item) => item.value === submissionType);
    return option ? option.label : undefined;
  }
}
