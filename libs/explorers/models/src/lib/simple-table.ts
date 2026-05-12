export interface SimpleTableColumn {
  name: string;
  tooltip?: string;
  align?: 'left' | 'right' | 'center';
  width?: string;
}

export interface SimpleTableTextCell {
  type: 'text';
  value: string | number;
  italic?: boolean;
}

export interface SimpleTableLabelCell {
  type: 'label';
  text: string;
}

export interface SimpleTableLinkCell {
  type: 'link';
  text: string;
  href: string;
}

export interface SimpleTableImageCell {
  type: 'image';
  src: string;
  alt: string;
}

export interface SimpleTableSwatchCell {
  type: 'swatch';
  color: string;
  text: string;
}

export type SimpleTableCell =
  | SimpleTableTextCell
  | SimpleTableLabelCell
  | SimpleTableLinkCell
  | SimpleTableImageCell
  | SimpleTableSwatchCell;
