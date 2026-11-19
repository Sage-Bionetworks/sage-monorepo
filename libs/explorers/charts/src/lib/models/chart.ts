export type ChartStyle = 'minimal' | 'grayGrid';

export interface BaseChartTheme {
  pointSymbolSize: number;
}

/**
 * Space between the chart's edges and its plot area (the region the data is drawn in), in
 * pixels. Each chart documents what its margins are measured to.
 */
export type PlotAreaMargins = {
  top?: number;
  right?: number;
  bottom?: number;
  left?: number;
};
