// Constants for the Comparison Tool Table component
// Minimum and maximum column widths in pixels
// These constants can be used to ensure that columns in the comparison tool table
// do not become too narrow or too wide, especially when the table is displaying
// narrow visualizations such as heat maps.
export const MIN_COLUMN_WIDTH_PX = 80;
export const MAX_COLUMN_WIDTH_PX = 300;

// CSS class applied to column header elements in the comparison tool table.
// Used during width measurement to temporarily override styles.
export const COLUMN_HEADER_CLASS = 'column-header';

// CSS class applied to the text container inside column headers.
export const COLUMN_HEADER_TEXT_CLASS = 'column-header-text';

// Left margin in pixels applied to column header content.
export const COLUMN_HEADER_MARGIN_LEFT_PX = 10;

// Right margin in pixels applied to column header content.
export const COLUMN_HEADER_MARGIN_RIGHT_PX = 22;

// Border width in pixels on each side of a column header cell.
export const COLUMN_HEADER_BORDER_WIDTH_PX = 1;

// Width in pixels of the sort icon in column headers.
export const SORT_ICON_WIDTH_PX = 14;
// Width in pixels of the multi-sort badge that PrimeNG
// renders next to sortable column headers.
export const SORT_BADGE_WIDTH_PX = 20;

// Width in pixels of the spacing next to the multi-sort badge
export const SORT_BADGE_SPACING_PX = 8;
