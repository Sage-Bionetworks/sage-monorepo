import { DateRange } from './data-range';

export interface FilterValue {
  /* The value of the filter. */
  value: DateRange | string | string[] | number | undefined;
  /* The display name of the filter value. */
  label: string | undefined;
  /* The avatar url of the filter value. */
  avatarUrl?: string | null;
}