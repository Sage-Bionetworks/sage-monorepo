import { Filter } from './filter.model';

export interface FilterPanel {
  /* The property name used to to query the data  */
  query: string;
  /* The label name of the filter  */
  label: string;
  /* The options of the filter. */
  options: Filter[];
  /* Whether this panel of filter is collapsed. */
  collapsed: boolean;
  /* Whether to show the avatar of filter value (now only applicable to dropdown filter). */
  showAvatar?: boolean;
}
