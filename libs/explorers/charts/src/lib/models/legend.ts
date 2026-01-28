import { ChartStyle } from './chart';

export interface PointStyle {
  label: string;
  color: string;
  shape: string;
  opacity: number;
}

export interface LegendProps {
  pointStyles: PointStyle[];
  chartStyle?: ChartStyle;
}
