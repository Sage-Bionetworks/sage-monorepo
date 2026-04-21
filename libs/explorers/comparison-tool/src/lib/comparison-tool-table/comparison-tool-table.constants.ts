// Constants for the Comparison Tool Table component.
//
// The *_PX values are consumed by measureCellWidths() in
// comparison-tool-table.helpers.ts to compute total column width from the
// measured header text. Each value must match its CSS counterpart.

// Clamping bounds for computed column widths. No CSS counterpart.
export const MIN_COLUMN_WIDTH_PX = 80;
export const MAX_COLUMN_WIDTH_PX = 300;

// CSS classes — bound in comparison-tool-columns.component.html, styled by the
// component's SCSS, and queried in helpers.ts.
export const COLUMN_HEADER_CLASS = 'column-header';
export const COLUMN_HEADER_TEXT_CLASS = 'column-header-text';

// Keep in sync with the corresponding $comparison-tool-* variables in
// libs/explorers/styles/src/lib/_variables.scss.
export const COLUMN_HEADER_MARGIN_LEFT_PX = 10;
export const COLUMN_HEADER_MARGIN_RIGHT_PX = 22;
export const SORT_BADGE_SPACING_PX = 8;

// PrimeNG-rendered sizes — no local CSS counterpart. Update if the PrimeNG
// theme or icon changes.
export const COLUMN_HEADER_BORDER_WIDTH_PX = 1; // th border (showGridlines)
export const SORT_ICON_WIDTH_PX = 14; // sort icon
export const SORT_BADGE_WIDTH_PX = 20; // multi-sort badge
