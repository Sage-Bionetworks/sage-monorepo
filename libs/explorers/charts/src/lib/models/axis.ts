export type AxisLineStyle = {
  width?: number;
  color?: string;
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
