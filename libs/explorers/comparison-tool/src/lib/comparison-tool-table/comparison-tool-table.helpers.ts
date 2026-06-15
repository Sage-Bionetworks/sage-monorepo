import { ComparisonToolColumn } from '@sagebionetworks/explorers/models';
import {
  COLUMN_HEADER_BORDER_WIDTH_PX,
  COLUMN_HEADER_CLASS,
  COLUMN_HEADER_MARGIN_LEFT_PX,
  COLUMN_HEADER_MARGIN_RIGHT_PX,
  COLUMN_HEADER_TEXT_CLASS,
  MAX_COLUMN_WIDTH_PX,
  MIN_COLUMN_WIDTH_PX,
  SORT_BADGE_SPACING_PX,
  SORT_BADGE_WIDTH_PX,
  SORT_ICON_WIDTH_PX,
} from './comparison-tool-table.constants';

export type SavedCell = {
  el: HTMLElement;
  style: string;
  descendants: { el: HTMLElement; ws: string }[];
};

export function getCellsByColumn(
  container: HTMLElement,
  columns: ComparisonToolColumn[],
): Map<string, HTMLElement[]> {
  const cellsByColumn = new Map<string, HTMLElement[]>();
  for (const column of columns) {
    const cells = Array.from(
      container.querySelectorAll(`[data-column-key="${CSS.escape(column.data_key)}"]`),
    ) as HTMLElement[];
    cellsByColumn.set(column.data_key, cells);
  }
  return cellsByColumn;
}

export function prepareCellsForMeasurement(
  cellsByColumn: Map<string, HTMLElement[]>,
  columns: ComparisonToolColumn[],
): { saved: SavedCell[]; savedHeaderWidths: Map<HTMLElement, string> } {
  const saved: SavedCell[] = [];
  const savedHeaderWidths = new Map<HTMLElement, string>();

  for (const column of columns) {
    const cells = cellsByColumn.get(column.data_key) || [];
    for (const el of cells) {
      const descEntries: { el: HTMLElement; ws: string }[] = [];
      el.querySelectorAll('*').forEach((d) => {
        const desc = d as HTMLElement;
        descEntries.push({ el: desc, ws: desc.style.whiteSpace });
        desc.style.whiteSpace = 'nowrap';
        // Remove width:100% on COLUMN_HEADER_CLASS so it sizes to content during measurement
        if (desc.classList.contains(COLUMN_HEADER_CLASS)) {
          savedHeaderWidths.set(desc, desc.style.width);
          desc.style.width = 'auto';
        }
      });
      saved.push({ el, style: el.getAttribute('style') || '', descendants: descEntries });

      // position:absolute takes the cell out of the flex row so it sizes to content.
      // visibility:hidden prevents flash. All CSS selectors and icon fonts still apply
      // because the element stays in its original DOM tree.
      el.style.position = 'absolute';
      el.style.width = 'auto';
      el.style.visibility = 'hidden';
      el.style.flex = 'none';
      el.style.whiteSpace = 'nowrap';
      el.style.overflow = 'visible';
      el.style.maxWidth = 'none';
    }
  }

  return { saved, savedHeaderWidths };
}

export function measureCellWidths(
  cellsByColumn: Map<string, HTMLElement[]>,
  columns: ComparisonToolColumn[],
): Record<string, number> {
  const rawWidths: Record<string, number> = {};
  for (const column of columns) {
    const cells = cellsByColumn.get(column.data_key) || [];
    let maxWidth = 0;
    for (const el of cells) {
      let width: number;
      if (el.tagName === 'TH') {
        // For header cells, measure only the text element and add fixed
        // widths for sort chrome so we don't depend on PrimeNG class names.
        const textEl = el.querySelector(`.${COLUMN_HEADER_TEXT_CLASS}`);
        const textWidth = textEl
          ? Math.ceil(textEl.getBoundingClientRect().width) + 1
          : Math.ceil(el.getBoundingClientRect().width) + 1;
        width =
          COLUMN_HEADER_BORDER_WIDTH_PX +
          COLUMN_HEADER_MARGIN_LEFT_PX +
          textWidth +
          SORT_ICON_WIDTH_PX +
          SORT_BADGE_WIDTH_PX +
          SORT_BADGE_SPACING_PX +
          COLUMN_HEADER_MARGIN_RIGHT_PX +
          COLUMN_HEADER_BORDER_WIDTH_PX;
      } else {
        // Use getBoundingClientRect for subpixel accuracy, ceil + 1px buffer
        // to prevent rounding-induced wrapping
        width = Math.ceil(el.getBoundingClientRect().width) + 1;
      }
      maxWidth = Math.max(maxWidth, width);
    }
    rawWidths[column.data_key] = maxWidth;
  }
  return rawWidths;
}

// Restore all cells (no paint occurred — all synchronous in one JS frame)
export function restoreCellStyles(
  saved: SavedCell[],
  savedHeaderWidths: Map<HTMLElement, string>,
): void {
  for (const { el, style, descendants } of saved) {
    if (style) {
      el.setAttribute('style', style);
    } else {
      el.removeAttribute('style');
    }
    for (const { el: desc, ws } of descendants) {
      desc.style.whiteSpace = ws;
      if (desc.classList.contains(COLUMN_HEADER_CLASS)) {
        desc.style.width = savedHeaderWidths.get(desc) || '';
      }
    }
  }
}

// Splits columns by whether they have an explicit column_width, resolving the fixed widths in
// the process. Columns with a column_width skip auto-sizing entirely — their width is applied
// verbatim (bypassing the MIN/MAX clamp) so it stays constant across sorts. Returns:
//  - fixedWidthByColumn: a `data_key -> "{n}px"` lookup for the fixed-width columns
//  - columnsToMeasure: the remaining columns, which the caller still auto-sizes by measurement
export function resolveFixedColumnWidths(columns: ComparisonToolColumn[]): {
  fixedWidthByColumn: Record<string, string>;
  columnsToMeasure: ComparisonToolColumn[];
} {
  const fixedWidthByColumn: Record<string, string> = {};
  const columnsToMeasure: ComparisonToolColumn[] = [];
  for (const column of columns) {
    if (column.column_width != null) {
      fixedWidthByColumn[column.data_key] = `${column.column_width}px`;
    } else {
      columnsToMeasure.push(column);
    }
  }
  return { fixedWidthByColumn, columnsToMeasure };
}

export function clampAndFormatWidths(rawWidths: Record<string, number>): Record<string, string> {
  const result: Record<string, string> = {};
  for (const key of Object.keys(rawWidths)) {
    const clamped = Math.min(Math.max(rawWidths[key], MIN_COLUMN_WIDTH_PX), MAX_COLUMN_WIDTH_PX);
    result[key] = clamped + 'px';
  }
  return result;
}
