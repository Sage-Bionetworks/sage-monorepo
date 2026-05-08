export interface SimpleTableColumn {
  name: string;
  tooltip?: string;
  align?: 'left' | 'right' | 'center';
}

export interface SimpleTableTextCell {
  kind: 'text';
  value: string | number;
}

export interface SimpleTableLabelCell {
  kind: 'label';
  text: string;
}

export interface SimpleTableLinkCell {
  kind: 'link';
  text: string;
  href: string;
}

export interface SimpleTableImageCell {
  kind: 'image';
  src: string;
  alt: string;
}

export type SimpleTableCell =
  | SimpleTableTextCell
  | SimpleTableLabelCell
  | SimpleTableLinkCell
  | SimpleTableImageCell;
