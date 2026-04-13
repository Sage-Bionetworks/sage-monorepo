import { MIN_COLUMN_WIDTH } from './comparison-tool-table.constants';

/**
 * Measures the natural content width of a column header <th>.
 * Sums the text span's scrollWidth, the sort area's scrollWidth,
 * and padding from the container elements. Both the span and the
 * sort div have overflow:hidden, so scrollWidth reports their full
 * content width even when collapsed.
 */
export function measureHeaderContentWidth(th: HTMLElement): number {
  const header = th.querySelector('.column-header') as HTMLElement | null;
  if (!header) return MIN_COLUMN_WIDTH;

  const textDiv = header.querySelector('.column-header-text') as HTMLElement | null;
  const textSpan = textDiv?.querySelector('span') as HTMLElement | null;
  const sortDiv = header.querySelector('.column-header-sort') as HTMLElement | null;

  const textStyle = textDiv ? getComputedStyle(textDiv) : null;
  const textPadding = textStyle
    ? parseFloat(textStyle.paddingLeft) + parseFloat(textStyle.paddingRight)
    : 0;
  const textWidth = textSpan ? textSpan.scrollWidth : 0;
  const sortWidth = sortDiv ? sortDiv.scrollWidth : 0;
  const headerPaddingRight = parseFloat(getComputedStyle(header).paddingRight) || 0;

  return textWidth + textPadding + sortWidth + headerPaddingRight;
}
