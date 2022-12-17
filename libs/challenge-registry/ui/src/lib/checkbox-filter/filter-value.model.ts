import { DateRange } from '@sagebionetworks/api-client-angular-deprecated';

export interface FilterValue {
  /* The value of the filter. */
  value: DateRange | string | undefined;
  /* The display name of the filter value. */
  label: string;
  /* Whether this filter value is active. */
  active: boolean;
}
