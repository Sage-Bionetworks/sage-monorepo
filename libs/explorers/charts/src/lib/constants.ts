import type { TooltipOption } from './types';

export const DEFAULT_POINT_SIZE = 18;
export const DEFAULT_COLOR = '#8b8ad1';
export const GRAY_BACKGROUND_COLOR = '#AEB5BC1A';
export const Y_AXIS_TICK_LABELS_MAX_WIDTH = 80;
export const CAPPED_POINT_SYMBOL = 'path://M0,0 L11,13 L5.5,13 L0,6.5 L-5.5,13 L-11,13 Z';
export const CAPPED_POINT_SYMBOL_SIZE = 12;
export const CAPPED_POINT_SPACING_PX = 15;
export const CAPPED_POINT_SERIES_NAME_SUFFIX = ' above y-axis max';
export const AUTO_Y_AXIS_MARGIN = '5%';

export const DARK_TOOLTIP: TooltipOption = {
  confine: true,
  position: 'top',
  backgroundColor: '#63676C',
  borderColor: 'transparent',
  textStyle: { color: 'white' },
  extraCssText:
    'opacity: 1 !important; width: auto; max-width: 300px; white-space: pre-wrap; text-align: center;',
};

export const LIGHT_TOOLTIP: TooltipOption = {
  ...DARK_TOOLTIP,
  backgroundColor: 'white',
  borderRadius: 0,
  textStyle: { color: '#22252A' },
};
