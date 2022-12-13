export interface FilterValue {
  /* The value of the filter. */
  value: { start?: Date; end?: Date } | string | undefined;
  /* The display name of the filter value. */
  label: string;
  /* Whether this filter value is active. */
  active: boolean;
}
