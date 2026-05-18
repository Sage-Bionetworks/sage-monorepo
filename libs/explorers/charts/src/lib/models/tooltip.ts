import type { EChartsOption } from 'echarts';

// EChartsOption['tooltip'] is `TooltipOption | TooltipOption[] | undefined`. We never
// use the array or undefined form, so narrow to the single-object case. NonNullable
// strips undefined before Exclude removes the array variant (Exclude alone leaves
// `undefined` since it isn't assignable to `unknown[]`). Importing from
// `echarts/components` instead triggers nominal-type mismatches when callers assign
// the result back into EChartsOption['tooltip'].
export type TooltipOption = Exclude<NonNullable<EChartsOption['tooltip']>, unknown[]>;

/**
 * Tooltip overrides. Any field provided replaces the corresponding value on the
 * chart's default tooltip; everything else (confine, extraCssText, max-width, etc.)
 * is inherited.
 */
export type TooltipStyle = {
  backgroundColor?: string;
  /** Text color. */
  color?: string;
  borderColor?: string;
};
