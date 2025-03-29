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

export interface RowChartItemValue {
  adj_p_val: number;
  fc: number;
  logfc: number;
}

export interface RowChartItem {
  key: string[];
  value: RowChartItemValue;
  tissue: string;
  ci_l: number;
  ci_r: number;
}
