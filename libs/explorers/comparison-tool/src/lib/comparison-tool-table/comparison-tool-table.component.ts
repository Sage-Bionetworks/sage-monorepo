import {
  AfterViewInit,
  Component,
  computed,
  effect,
  ElementRef,
  HostListener,
  inject,
  signal,
  viewChild,
  ViewEncapsulation,
} from '@angular/core';
import {
  getPinAllTooltip,
  getPinnedFetchFailedMessage,
  getPinnedResultsControlsCopy,
  getPinnedResultsLabels,
  getViewNounLabel,
  TABLE_DATA_LOADING_MESSAGE,
} from '@sagebionetworks/explorers/constants';
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
  COMPARISON_TOOL_BODY_CLASS,
  PINNED_RESULTS_CONTROLS,
} from './comparison-tool-table.constants';
import {
  clampAndFormatWidths,
  getCellsByColumn,
  measureCellWidths,
  prepareCellsForMeasurement,
  resolveFixedColumnWidths,
  restoreCellStyles,
} from './comparison-tool-table.helpers';
import { FetchFailedBarComponent } from './fetch-failed-bar/fetch-failed-bar.component';

@Component({
  selector: 'explorers-comparison-tool-table',
  imports: [
    TooltipModule,
    ComparisonToolColumnsComponent,
    SvgIconComponent,
    BaseTableComponent,
    DownloadDomImageComponent,
    FetchFailedBarComponent,
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

  pinnedRowCount = this.comparisonToolService.pinnedRowCount;
  pinnedResultsCounts = this.comparisonToolService.pinnedResultsCounts;
  nounsForPinCount = this.comparisonToolService.nounsForPinCount;
  nouns = this.comparisonToolService.nouns;
  canPinAll = this.comparisonToolService.canPinAll;
  disabledPinTooltip = this.comparisonToolService.disabledPinTooltip;
  isLoadingTableData = this.comparisonToolService.isLoadingTableData;
  isLoadingPinnedData = this.comparisonToolService.isLoadingPinnedData;
  pinnedFetchFailed = this.comparisonToolService.pinnedFetchFailed;
  unpinnedRowCount = this.comparisonToolService.unpinnedRowCount;
  viewConfig = this.comparisonToolService.viewConfig;

  searchTerm = this.comparisonToolFilterService.searchTerm;
  hasSelectedFilters = this.comparisonToolFilterService.hasSelectedFilters;

  selectedColumns = this.comparisonToolService.selectedColumns;

  pinnedData = this.comparisonToolService.pinnedData;
  unpinnedData = this.comparisonToolService.unpinnedData;

  columnWidths = signal<Record<string, string>>({});

  readonly pinnedResultsControls = PINNED_RESULTS_CONTROLS;
  pinnedResultsControlsCopy = computed(() => getPinnedResultsControlsCopy(this.nouns()));

  pinnedResultsLabels = computed(() =>
    getPinnedResultsLabels(this.pinnedResultsCounts(), this.nounsForPinCount()),
  );

  hasPinnedSection = computed(() => this.pinnedRowCount() > 0 || this.pinnedFetchFailed());

  pinnedFetchFailedMessage = computed(() => getPinnedFetchFailedMessage(this.nouns()));

  unpinnedResultsLabel = computed(() =>
    getViewNounLabel(this.searchTerm() ? 'Matching' : 'Filtered', this.nouns()),
  );

  allResultsLabel = computed(() => getViewNounLabel('All', this.nouns()));

  pinAllTooltip = computed(() => {
    if (!this.canPinAll()) return this.disabledPinTooltip();
    return this.isLoadingTableData() ? TABLE_DATA_LOADING_MESSAGE : getPinAllTooltip(this.nouns());
  });

  downloadPinsTooltip = computed(() =>
    this.isLoadingTableData()
      ? TABLE_DATA_LOADING_MESSAGE
      : this.pinnedResultsControlsCopy().downloadButtonTooltip,
  );

  clearAllPinsTooltip = computed(() =>
    this.isLoadingTableData()
      ? TABLE_DATA_LOADING_MESSAGE
      : this.pinnedResultsControlsCopy().clearButtonTooltip,
  );

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
        // Measurement briefly pulls cells out of flow, collapsing the table so the
        // browser clamps the scroll container's scrollLeft. Save and restore it so
        // sorting/data changes don't reset the user's horizontal scroll position.
        const scrollContainer = this.tableElement()?.nativeElement.closest(
          `.${COMPARISON_TOOL_BODY_CLASS}`,
        ) as HTMLElement | null;
        const savedScrollLeft = scrollContainer?.scrollLeft ?? 0;
        this.columnWidths.set(this.calculateNonPrimaryColumnWidths());
        if (scrollContainer) {
          scrollContainer.scrollLeft = savedScrollLeft;
        }
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
    this.comparisonToolService.pinAll();
  }

  clearAllPinned() {
    this.comparisonToolService.resetPinnedItems();
  }

  retryPinnedFetch() {
    this.comparisonToolService.retryPinnedFetch();
  }

  // Calculate widths for non-primary columns since primary columns have fixed widths in the design
  calculateNonPrimaryColumnWidths(): Record<string, string> {
    const container: HTMLElement | undefined = this.tableElement()?.nativeElement;
    if (!container) return {};

    const nonPrimaryColumns = this.selectedColumns().filter((col) => col.type !== 'primary');
    if (nonPrimaryColumns.length === 0) return {};

    // Columns with an explicit column_width skip auto-sizing entirely so their width stays
    // constant across sorts. These widths are applied verbatim, bypassing the MIN/MAX clamp.
    const { fixedWidthByColumn, columnsToMeasure } = resolveFixedColumnWidths(nonPrimaryColumns);

    if (columnsToMeasure.length === 0) return fixedWidthByColumn;

    const cellsByColumn = getCellsByColumn(container, columnsToMeasure);
    const { saved, savedHeaderWidths } = prepareCellsForMeasurement(
      cellsByColumn,
      columnsToMeasure,
    );

    const rawWidths = measureCellWidths(cellsByColumn, columnsToMeasure);

    restoreCellStyles(saved, savedHeaderWidths);

    return { ...clampAndFormatWidths(rawWidths), ...fixedWidthByColumn };
  }
}
