import { DateRange } from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';

export interface FilterValue {
  /* The value of the filter. */
  value: DateRange | string | string[] | undefined;
  /* The display name of the filter value. */
  label: string | undefined;
  /* The avatar url of the filter value. */
  avatarUrl?: string | null;
  /* Whether this filter value is active. */
  active: boolean;
}
