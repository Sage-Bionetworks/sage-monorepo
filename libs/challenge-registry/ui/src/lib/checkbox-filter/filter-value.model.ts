import { DateRange } from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';

export interface FilterValue {
  /* The value of the filter. */
  value: DateRange | string | undefined;
  /* The display name of the filter value. */
  label: string | undefined;
  /* Whether this filter value is active. */
  active: boolean;
}
