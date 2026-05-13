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
