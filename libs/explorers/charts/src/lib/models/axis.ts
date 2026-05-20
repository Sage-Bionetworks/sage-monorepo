export type AxisLineStyle = {
  width?: number;
  color?: string;
};

export type AxisTickLabelStyle = {
  fontSize?: string;
  fontWeight?: 'normal' | 'bold' | 'bolder' | 'lighter' | number;
  color?: string;
};

export type GridLineStyle = {
  width: number;
  color: string;
  type: 'solid' | 'dashed' | 'dotted';
};
