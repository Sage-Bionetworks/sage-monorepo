export interface SimpleTableColumn {
  name: string;
  tooltip?: string;
  align?: 'left' | 'right' | 'center';
}

export interface SimpleTableTextCell {
  type: 'text';
  value: string | number;
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

export type SimpleTableCell =
  | SimpleTableTextCell
  | SimpleTableLabelCell
  | SimpleTableLinkCell
  | SimpleTableImageCell;
