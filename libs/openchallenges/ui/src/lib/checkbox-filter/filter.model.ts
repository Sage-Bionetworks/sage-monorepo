import { FilterValue } from './filter-value.model';

export interface Filter {
  /* The property name used to to query the data  */
  query: string;
  /* The label name of the filter  */
  label: string;
  /* The values of the filter. */
  values: FilterValue[];
  /* Whether this panel of filter is collapsed. */
  collapsed: boolean;
  /* Whether to show the avatar of filter value (now only applicable to dropdown filter). */
  showAvatar?: boolean;
}
