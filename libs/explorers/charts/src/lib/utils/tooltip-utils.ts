import { TooltipStyle } from '../models';
import { TooltipOption } from '../types';

// ECharts draws an arrow under the tooltip whenever `position` is a string like 'top'. Turning
// off `confine` keeps the tooltip (and its arrow) on the hovered element instead of being pushed
// inside the chart, and appending it to the body keeps it from being clipped by its container.
const PIN_ABOVE_TOOLTIP: TooltipOption = {
  position: 'top',
  confine: false,
  appendTo: 'body',
};

function buildExtraCssText(base: TooltipOption, style: TooltipStyle): string | undefined {
  if (!style.textAlign) return base.extraCssText;
  // Appended last so it overrides any text-align already in the base CSS.
  return `${base.extraCssText ?? ''} text-align: ${style.textAlign};`.trim();
}

function buildTextStyle(base: TooltipOption, style: TooltipStyle): TooltipOption['textStyle'] {
  return {
    ...base.textStyle,
    ...(style.color !== undefined && { color: style.color }),
    ...(style.fontFamily !== undefined && { fontFamily: style.fontFamily }),
    ...(style.lineHeight !== undefined && { lineHeight: style.lineHeight }),
  };
}

// Merges TooltipStyle overrides onto a base tooltip config. Fields not present
// on `style` fall through, so callers only need to specify what they want to
// change (e.g. just backgroundColor) and everything else (confine, extraCssText,
// max-width, etc.) is inherited from `base`.
export function buildTooltip(base: TooltipOption, style: TooltipStyle | undefined): TooltipOption {
  if (!style) return base;
  return {
    ...base,
    ...(style.backgroundColor !== undefined && { backgroundColor: style.backgroundColor }),
    ...(style.borderColor !== undefined && { borderColor: style.borderColor }),
    ...(style.padding !== undefined && { padding: style.padding }),
    ...(style.borderRadius !== undefined && { borderRadius: style.borderRadius }),
    textStyle: buildTextStyle(base, style),
    ...(style.pinAbove && PIN_ABOVE_TOOLTIP),
    extraCssText: buildExtraCssText(base, style),
  };
}
