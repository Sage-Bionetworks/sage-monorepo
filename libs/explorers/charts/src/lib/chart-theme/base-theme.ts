import { DEFAULT_POINT_SIZE } from '../constants';
import { BaseChartTheme } from '../models/chart';

export const minimalBaseChartTheme: BaseChartTheme = {
  pointSymbolSize: DEFAULT_POINT_SIZE,
};

export const grayGridBaseChartTheme: BaseChartTheme = {
  pointSymbolSize: DEFAULT_POINT_SIZE / 2,
};
