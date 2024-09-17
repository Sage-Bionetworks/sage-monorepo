export interface boxPlotChartItemCircle {
  value: number;
  tooltip: string;
}

export interface boxPlotChartItem {
  key: string;
  value: number[];
  circle: boxPlotChartItemCircle;
  quartiles: number[];
}

export interface rowChartItemValue {
  adj_p_val: number;
  fc: number;
  logfc: number;
}

export interface rowChartItem {
  key: string[];
  value: rowChartItemValue;
  tissue: string;
  ci_l: number;
  ci_r: number;
}
