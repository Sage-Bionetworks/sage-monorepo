export interface BoxPlotChartItemCircle {
  value: number;
  tooltip: string;
}

export interface BoxPlotChartItem {
  key: string;
  value: number[];
  circle?: BoxPlotChartItemCircle;
  quartiles: number[];
}
