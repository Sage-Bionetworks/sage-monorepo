import {
  AfterViewInit,
  Component,
  effect,
  ElementRef,
  HostListener,
  inject,
  viewChild,
  ViewEncapsulation,
} from '@angular/core';
import {
  ComparisonToolFilterService,
  ComparisonToolHelperService,
  ComparisonToolService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import { DownloadDomImageComponent } from '@sagebionetworks/explorers/ui';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { TooltipModule } from 'primeng/tooltip';
import { BaseTableComponent } from './base-table/base-table.component';
import { ComparisonToolColumnsComponent } from './comparison-tool-columns/comparison-tool-columns.component';
import { MAX_COLUMN_WIDTH_PX, MIN_COLUMN_WIDTH_PX } from './comparison-tool-table.constants';

@Component({
  selector: 'explorers-comparison-tool-table',
  imports: [
    TooltipModule,
    ComparisonToolColumnsComponent,
    SvgIconComponent,
    BaseTableComponent,
    DownloadDomImageComponent,
  ],
  templateUrl: './comparison-tool-table.component.html',
  styleUrls: ['./comparison-tool-table.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolTableComponent implements AfterViewInit {
  comparisonToolService = inject(ComparisonToolService);
  comparisonToolFilterService = inject(ComparisonToolFilterService);
  comparisonToolHelperService = inject(ComparisonToolHelperService);
  platformService = inject(PlatformService);

  tableElement = viewChild<ElementRef>('table');

  pinnedResultsCount = this.comparisonToolService.pinnedResultsCount;
  maxPinnedItems = this.comparisonToolService.maxPinnedItems;
  hasMaxPinnedItems = this.comparisonToolService.hasMaxPinnedItems;
  disabledPinTooltip = this.comparisonToolService.disabledPinTooltip;
  totalResultsCount = this.comparisonToolService.totalResultsCount;
  viewConfig = this.comparisonToolService.viewConfig;

  searchTerm = this.comparisonToolFilterService.searchTerm;
  hasSelectedFilters = this.comparisonToolFilterService.hasSelectedFilters;

  selectedColumns = this.comparisonToolService.selectedColumns;

  pinnedData = this.comparisonToolService.pinnedData;
  unpinnedData = this.comparisonToolService.unpinnedData;

  columnWidths: Record<string, string> = {};

  constructor() {
    if (this.platformService.isBrowser) {
      // Recalculate column widths whenever selected columns, pinned data, or unpinned data change
      effect((onCleanup) => {
        // Access the signals to track changes
        this.selectedColumns();
        this.pinnedData();
        this.unpinnedData();
        const timeoutId = setTimeout(() => {
          this.recalculateColumnWidths();
        }, 0);
        onCleanup(() => clearTimeout(timeoutId));
      });
    }
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.recalculateColumnWidths();
    }, 100);
  }

  @HostListener('window:resize')
  recalculateColumnWidths() {
    if (this.platformService.isBrowser) {
      // icon fonts may not be loaded yet on initial render,
      // so wait for fonts to be ready before calculating column widths
      // to prevent incorrect measurements
      document.fonts.ready.then(() => {
        this.columnWidths = this.calculateColumnWidths();
      });
    }
  }

  getPinnedDataFilename(): string {
    const config = this.comparisonToolService.currentConfig();
    if (!config) return '';
    return this.comparisonToolHelperService.getComparisonToolDataFilename(config);
  }

  getPinnedDataForCsv(): string[][] {
    const config = this.comparisonToolService.currentConfig();
    if (!config) return [];

    const data = this.pinnedData();
    const siteUrl = window.location.origin;
    return this.comparisonToolHelperService.buildComparisonToolCsvRows(
      data,
      config,
      siteUrl,
      'heatmap',
      this.viewConfig().linkExportField,
    );
  }

  pinAll() {
    // TODO: handle pagination (i.e. unpinnedData only contains the first page of data, rather than all unpinned data)
    const rowIdDataKey = this.viewConfig().rowIdDataKey;
    this.comparisonToolService.pinList(this.unpinnedData().map((item) => item[rowIdDataKey]));
  }

  clearAllPinned() {
    this.comparisonToolService.resetPinnedItems();
  }

  calculateColumnWidths(): Record<string, string> {
    const container: HTMLElement | undefined = this.tableElement()?.nativeElement;
    if (!container) return {};

    const columns = this.selectedColumns();
    const nonPrimaryColumns = columns.filter((col) => col.type !== 'primary');
    if (nonPrimaryColumns.length === 0) return {};

    // Collect all non-primary cells grouped by column, saving original inline styles
    const cellsByColumn = new Map<string, HTMLElement[]>();
    const savedHeaderWidths = new Map<HTMLElement, string>();
    const saved: {
      el: HTMLElement;
      style: string;
      descendants: { el: HTMLElement; ws: string }[];
    }[] = [];

    for (const column of nonPrimaryColumns) {
      const cells = Array.from(
        container.querySelectorAll(`[data-column-key="${column.data_key}"]`),
      ) as HTMLElement[];
      cellsByColumn.set(column.data_key, cells);

      for (const el of cells) {
        const descEntries: { el: HTMLElement; ws: string }[] = [];
        el.querySelectorAll('*').forEach((d) => {
          const desc = d as HTMLElement;
          descEntries.push({ el: desc, ws: desc.style.whiteSpace });
          desc.style.whiteSpace = 'nowrap';
          // Remove width:100% on .column-header so it sizes to content during measurement
          if (desc.classList.contains('column-header')) {
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

    // Measure all cells (single reflow).
    // For <th> cells without a badge, temporarily inject a dummy badge inside the
    // <p-sorticon> element (where PrimeNG places the real one) so the measurement
    // reflects the worst-case width when multi-sort is active.
    const injectedBadges: { el: HTMLElement; parent: HTMLElement }[] = [];
    for (const column of nonPrimaryColumns) {
      const cells = cellsByColumn.get(column.data_key) || [];
      for (const el of cells) {
        if (el.tagName === 'TH' && !el.querySelector('.p-sortable-column-badge')) {
          const sortIcon = el.querySelector('p-sorticon');
          if (sortIcon) {
            const dummy = document.createElement('p-badge');
            dummy.className =
              'p-sortable-column-badge p-badge p-badge-circle p-badge-sm p-component';
            dummy.textContent = '0';
            sortIcon.appendChild(dummy);
            injectedBadges.push({ el: dummy, parent: sortIcon as HTMLElement });
          }
        }
      }
    }

    const idealWidths: Record<string, number> = {};
    for (const column of nonPrimaryColumns) {
      const cells = cellsByColumn.get(column.data_key) || [];
      let maxWidth = 0;
      for (const el of cells) {
        // Use getBoundingClientRect for subpixel accuracy, ceil + 1px buffer
        // to prevent rounding-induced wrapping
        const width = Math.ceil(el.getBoundingClientRect().width) + 1;
        maxWidth = Math.max(maxWidth, width);
      }
      idealWidths[column.data_key] = Math.min(
        Math.max(maxWidth, MIN_COLUMN_WIDTH_PX),
        MAX_COLUMN_WIDTH_PX,
      );
    }

    // Remove injected dummy badges
    for (const { el, parent } of injectedBadges) {
      parent.removeChild(el);
    }

    // Restore all cells (no paint occurred — all synchronous in one JS frame)
    for (const { el, style, descendants } of saved) {
      if (style) {
        el.setAttribute('style', style);
      } else {
        el.removeAttribute('style');
      }
      for (const { el: desc, ws } of descendants) {
        desc.style.whiteSpace = ws;
        if (desc.classList.contains('column-header')) {
          desc.style.width = savedHeaderWidths.get(desc) || '';
        }
      }
    }

    const result: Record<string, string> = {};
    for (const key of Object.keys(idealWidths)) {
      result[key] = idealWidths[key] + 'px';
    }

    return result;
  }
}
