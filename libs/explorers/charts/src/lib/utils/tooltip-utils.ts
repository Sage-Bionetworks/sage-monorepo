import { TooltipStyle } from '../models';
import { TooltipOption } from '../types';

/**
 * Class on a pinned-above tooltip. Apps that set `pinAbove` must load the explorers styles, which
 * draw the notch for this class in the color of TOOLTIP_NOTCH_COLOR_PROPERTY.
 */
export const TOOLTIP_NOTCH_CLASS = 'explorers-chart-tooltip-notch';
export const TOOLTIP_NOTCH_COLOR_PROPERTY = '--explorers-chart-tooltip-notch-color';

// Distance from the hovered element to the bottom of the tooltip box. It must exceed the
// stylesheet's notch height so the notch tip stops just short of the element.
export const PIN_ABOVE_GAP_PX = 9;

type TooltipPositionCallback = Extract<NonNullable<TooltipOption['position']>, CallableFunction>;

// Centers the tooltip above the hovered element, or above the pointer when there is no element.
// It's a function rather than ECharts' 'top' because a string position adds ECharts' own arrow,
// which the stylesheet's notch replaces.
export const pinAbovePosition: TooltipPositionCallback = (point, _params, _dom, rect, size) => {
  const [contentWidth, contentHeight] = size.contentSize;
  const anchorX = rect ? rect.x + rect.width / 2 : point[0];
  const anchorY = rect ? rect.y : point[1];
  return [anchorX - contentWidth / 2, anchorY - contentHeight - PIN_ABOVE_GAP_PX];
};

// Turning off `confine` keeps the tooltip on the hovered element instead of being pushed inside
// the chart, and appending it to the body keeps it from being clipped by its container.
const PIN_ABOVE_TOOLTIP: TooltipOption = {
  position: pinAbovePosition,
  confine: false,
  appendTo: 'body',
  className: TOOLTIP_NOTCH_CLASS,
};

function buildExtraCssText(base: TooltipOption, style: TooltipStyle): string | undefined {
  const notchColor = style.backgroundColor ?? base.backgroundColor;
  const declarations = [
    // Appended after the base CSS so it overrides any text-align already there.
    style.textAlign && `text-align: ${style.textAlign};`,
    style.pinAbove && notchColor && `${TOOLTIP_NOTCH_COLOR_PROPERTY}: ${notchColor};`,
  ].filter(Boolean);
  if (!declarations.length) return base.extraCssText;
  return [base.extraCssText, ...declarations].filter(Boolean).join(' ');
}

function buildTextStyle(base: TooltipOption, style: TooltipStyle): TooltipOption['textStyle'] {
  return {
    ...base.textStyle,
    ...(style.color !== undefined && { color: style.color }),
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
