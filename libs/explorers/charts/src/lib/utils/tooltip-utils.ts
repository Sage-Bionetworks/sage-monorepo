import { EChartsOption } from 'echarts';
import { TooltipStyle } from '../models';

// Merges TooltipStyle overrides onto a base tooltip config. Fields not present
// on `style` fall through, so callers only need to specify what they want to
// change (e.g. just backgroundColor) and everything else (confine, extraCssText,
// max-width, etc.) is inherited from `base`.
export function buildTooltip(
  base: EChartsOption['tooltip'],
  style: TooltipStyle | undefined,
): EChartsOption['tooltip'] {
  if (!style) return base;
  const baseTextStyle = (base as { textStyle?: object } | undefined)?.textStyle;
  return {
    ...base,
    ...(style.backgroundColor !== undefined && { backgroundColor: style.backgroundColor }),
    ...(style.borderColor !== undefined && { borderColor: style.borderColor }),
    ...(style.color !== undefined && { textStyle: { ...baseTextStyle, color: style.color } }),
  };
}
