import {
  AfterViewInit,
  Component,
  effect,
  ElementRef,
  HostListener,
  inject,
  signal,
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
import {
  clampAndFormatWidths,
  getCellsByColumn,
  measureCellWidths,
  prepareCellsForMeasurement,
  restoreCellStyles,
} from './comparison-tool-table.helpers';

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

  columnWidths = signal<Record<string, string>>({});

  constructor() {
    if (this.platformService.isBrowser) {
      effect((onCleanup) => {
        // Recalculate column widths whenever selectedColumns, pinnedData, or unpinnedData changes
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
        this.columnWidths.set(this.calculateNonPrimaryColumnWidths());
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

  // Calculate widths for non-primary columns since primary columns have fixed widths in the design
  calculateNonPrimaryColumnWidths(): Record<string, string> {
    const container: HTMLElement | undefined = this.tableElement()?.nativeElement;
    if (!container) return {};

    const nonPrimaryColumns = this.selectedColumns().filter((col) => col.type !== 'primary');
    if (nonPrimaryColumns.length === 0) return {};

    const cellsByColumn = getCellsByColumn(container, nonPrimaryColumns);
    const { saved, savedHeaderWidths } = prepareCellsForMeasurement(
      cellsByColumn,
      nonPrimaryColumns,
    );

    const rawWidths = measureCellWidths(cellsByColumn, nonPrimaryColumns);

    restoreCellStyles(saved, savedHeaderWidths);

    return clampAndFormatWidths(rawWidths);
  }
}
