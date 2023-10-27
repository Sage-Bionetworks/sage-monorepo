import { DateRange } from './data-range';

export type FilterValue = DateRange | string | number | undefined;

export interface Filter {
  /* The value of the filter. */
  value: FilterValue | FilterValue[];
  /* The display name of the filter value. */
  label: string | undefined;
  /* The avatar url of the filter value. */
  avatarUrl?: string | null;
}
