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
  /**
   * If true, keeps the tooltip above the hovered element, with its arrow pointing down at it,
   * even near the chart's top edge. The tooltip is appended to the document body so it can
   * extend past the chart's edges.
   */
  pinAbove?: boolean;
  /** Text alignment inside the tooltip. Defaults to center. */
  textAlign?: 'left' | 'center' | 'right';
};
