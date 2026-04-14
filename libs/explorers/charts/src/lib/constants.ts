import type { EChartsOption } from 'echarts';

export const DEFAULT_POINT_SIZE = 18;
export const GRAY_BACKGROUND_COLOR = '#AEB5BC1A';
export const Y_AXIS_TICK_LABELS_MAX_WIDTH = 80;

export const DARK_TOOLTIP: EChartsOption['tooltip'] = {
  confine: true,
  position: 'top',
  backgroundColor: '#63676C',
  borderColor: 'transparent',
  textStyle: { color: 'white' },
  extraCssText:
    'opacity: 1 !important; width: auto; max-width: 300px; white-space: pre-wrap; text-align: center;',
};

export const LIGHT_TOOLTIP: EChartsOption['tooltip'] = {
  ...DARK_TOOLTIP,
  backgroundColor: 'white',
  borderRadius: 0,
  textStyle: { color: '#22252A' },
};
