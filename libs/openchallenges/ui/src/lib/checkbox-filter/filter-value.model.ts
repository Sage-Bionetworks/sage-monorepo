import { DateRange } from '@sagebionetworks/openchallenges/api-client-angular-deprecated';

export interface FilterValue {
  /* The value of the filter. */
  value: DateRange | string | string[] | undefined;
  /* The display name of the filter value. */
  label: string | undefined;
  /* The avatar url of the filter value. */
  avatarUrl?: string | null;
}
