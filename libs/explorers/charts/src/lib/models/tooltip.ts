import type { EChartsOption } from 'echarts';

// EChartsOption['tooltip'] is `TooltipOption | TooltipOption[] | undefined`. We never
// use the array form, so narrow to the single-object case. Importing from
// `echarts/components` instead triggers nominal-type mismatches when callers assign
// the result back into EChartsOption['tooltip'].
export type TooltipOption = Exclude<EChartsOption['tooltip'], unknown[]>;

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
