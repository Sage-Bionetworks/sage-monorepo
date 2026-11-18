export type AxisLineStyle = {
  width?: number;
  color?: string;
  /**
   * Line end shape. ECharts defaults axis lines to 'round', which extends each end by half the
   * width; use 'butt' when two axis lines must meet in a square corner.
   */
  cap?: 'butt' | 'round' | 'square';
};

export type AxisTickLabelStyle = {
  fontSize?: string;
  fontWeight?: 'normal' | 'bold' | 'bolder' | 'lighter' | number;
  color?: string;
};

export type GridMargins = {
  top?: number;
  right?: number;
  bottom?: number;
  left?: number;
};

export type GridLineStyle = {
  width: number;
  color: string;
  type: 'solid' | 'dashed' | 'dotted';
};
