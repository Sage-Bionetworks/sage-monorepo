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
  HelperService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import { DownloadDomImageComponent } from '@sagebionetworks/explorers/ui';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { TooltipModule } from 'primeng/tooltip';
import { BaseTableComponent } from './base-table/base-table.component';
import { ComparisonToolColumnsComponent } from './comparison-tool-columns/comparison-tool-columns.component';
import { MIN_COLUMN_WIDTH } from './comparison-tool-table.constants';

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
  helperService = inject(HelperService);
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

  primaryColumnWidth = 300;

  constructor() {
    if (this.platformService.isBrowser) {
      this.primaryColumnWidth = this.helperService.getNumberFromCSSValue(
        getComputedStyle(document.documentElement).getPropertyValue(
          '--comparison-tool-primary-column-width',
        ),
      );

      // Recalculate column widths whenever selected columns change
      effect((onCleanup) => {
        this.selectedColumns();
        const timeoutId = setTimeout(() => {
          this.onWindowResize();
        }, 0);
        onCleanup(() => clearTimeout(timeoutId));
      });
    }
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.onWindowResize();
    }, 100);
  }

  @HostListener('window:resize')
  onWindowResize() {
    if (this.platformService.isBrowser) {
      this.resetColumnWidths();
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
    const rowIdDataKey = this.viewConfig().rowIdDataKey;
    this.comparisonToolService.pinList(this.unpinnedData().map((item) => item[rowIdDataKey]));
  }

  clearAllPinned() {
    this.comparisonToolService.resetPinnedItems();
  }

  calculateNonprimaryColumnWidth(nCols: number, tableWidth: number) {
    const count = Math.max(nCols, 5);
    return Math.ceil((tableWidth - this.primaryColumnWidth) / count) + 'px';
  }

  /**
   * Sets CSS custom properties on the container element for each column width.
   * All cells in all three tables reference these variables, so one setProperty
   * call updates every cell in that column across all tables — no Angular
   * change detection involved.
   */
  private resetColumnWidths() {
    const container = this.tableElement()?.nativeElement as HTMLElement;
    if (!container) return;

    const tableElementWidth = container.offsetWidth || 0;
    const cols = this.selectedColumns();
    const nonPrimaryCount = cols.filter((c) => c.type !== 'primary').length;
    const defaultWidth = this.calculateNonprimaryColumnWidth(nonPrimaryCount, tableElementWidth);
    const primaryWidth = this.primaryColumnWidth + 'px';

    // Measure natural header widths so columns are never narrower than their header text.
    const headerWidths = this.measureHeaderWidths(container, cols.length);

    for (let i = 0; i < cols.length; i++) {
      let width: number;
      if (cols[i].type === 'primary') {
        width = this.primaryColumnWidth;
      } else if (cols[i].type === 'heat_map') {
        width = MIN_COLUMN_WIDTH;
      } else {
        width = parseInt(defaultWidth, 10) || MIN_COLUMN_WIDTH;
      }
      width = Math.max(width, headerWidths[i] || MIN_COLUMN_WIDTH);
      container.style.setProperty(`--ct-col-${i}-width`, width + 'px');
    }
  }

  /**
   * Temporarily collapses all columns to 0 and reads each .column-header's
   * scrollWidth plus .column-header-sort's scrollWidth. Both have overflow:hidden,
   * so scrollWidth reports their full content width even when collapsed to 0px.
   */
  private measureHeaderWidths(container: HTMLElement, colCount: number): number[] {
    const widths: number[] = [];

    // Shrink all columns to 0 so headers overflow
    for (let i = 0; i < colCount; i++) {
      container.style.setProperty(`--ct-col-${i}-width`, '0px');
    }

    void container.offsetWidth; // single reflow for all columns

    // Read widths from .column-header.scrollWidth + .column-header-sort.scrollWidth
    for (let i = 0; i < colCount; i++) {
      const th = container.querySelector(`th:nth-child(${i + 1})`) as HTMLElement | null;
      if (th) {
        const header = th.querySelector('.column-header') as HTMLElement | null;
        const sortDiv = th.querySelector('.column-header-sort') as HTMLElement | null;
        const headerWidth = header ? header.scrollWidth : 0;
        const sortWidth = sortDiv ? sortDiv.scrollWidth : 0;
        widths.push(Math.max(headerWidth + sortWidth, MIN_COLUMN_WIDTH));
      } else {
        widths.push(MIN_COLUMN_WIDTH);
      }
    }

    return widths;
  }
}
