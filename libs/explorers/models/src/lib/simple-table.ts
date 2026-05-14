export interface SimpleTableColumn {
  name: string;
  tooltip?: string;
  /**
   * Applied to the column's `<th>` and `<td>` elements. Because those elements
   * live inside SimpleTableComponent's view, the caller must use
   * `ViewEncapsulation.None` (or global styles) for their CSS rules to reach them.
   */
  className?: string;
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
