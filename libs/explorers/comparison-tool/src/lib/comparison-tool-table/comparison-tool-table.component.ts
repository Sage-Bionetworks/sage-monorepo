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

    for (let i = 0; i < cols.length; i++) {
      let width: string;
      if (cols[i].type === 'primary') {
        width = primaryWidth;
      } else if (cols[i].type === 'heat_map') {
        width = MIN_COLUMN_WIDTH + 'px';
      } else {
        width = defaultWidth;
      }
      container.style.setProperty(`--ct-col-${i}-width`, width);
    }
  }
}
