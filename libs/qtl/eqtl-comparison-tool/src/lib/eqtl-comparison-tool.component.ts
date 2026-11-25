import { Component, effect, inject, OnDestroy, OnInit } from '@angular/core';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import { ComparisonToolQuery, ComparisonToolViewConfig } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolUrlService,
  LoggerService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import {
  ComparisonToolConfigService,
  ComparisonToolPage,
  EqtlSearchQuery,
  EqtlService,
  EqtlsPage,
  ItemFilterTypeQuery,
} from '@sagebionetworks/qtl/api-client';
import { DEFAULT_SYNAPSE_WIKI_OWNER_ID } from '@sagebionetworks/qtl/config';
import { SortMeta } from 'primeng/api';
import { map, shareReplay } from 'rxjs';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { EqtlComparisonToolService } from './services/eqtl-comparison-tool.service';

@Component({
  selector: 'qtl-eqtl-comparison-tool',
  imports: [ComparisonToolComponent, SidebarComponent],
  templateUrl: './eqtl-comparison-tool.component.html',
  styleUrls: ['./eqtl-comparison-tool.component.scss'],
})
export class EqtlComparisonToolComponent implements OnInit, OnDestroy {
  private readonly platformService = inject(PlatformService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly eqtlService = inject(EqtlService);
  private readonly comparisonToolService = inject(EqtlComparisonToolService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);
  private readonly logger = inject(LoggerService).forSource('EqtlComparisonToolComponent');

  isInitialized = this.comparisonToolService.isInitialized;
  query = this.comparisonToolService.query;

  readonly config$ = this.comparisonToolConfigService
    .getComparisonToolConfig(ComparisonToolPage.EQtls)
    .pipe(shareReplay({ bufferSize: 1, refCount: true }));

  viewConfig: Partial<ComparisonToolViewConfig> = {
    headerTitle: ComparisonToolPage.EQtls,
    headerTitleWikiParams: { ownerId: DEFAULT_SYNAPSE_WIKI_OWNER_ID, wikiId: '641079' },
    filterResultsButtonTooltip: 'Filter results by variant, gene, cell type and more',
    showSignificanceControls: false,
    showTableSearch: false,
    legendEnabled: false,
    rowIdDataKey: 'composite_id',
    allowPinnedImageDownload: false,
    defaultSort: [{ field: 'adj_p_val', order: 1 }],
    rowSelectionEnabled: true,
    rowHoverEnabled: true,
    // TODO(EX-287): round the Beta and MAF columns on screen
    // TODO(EX-298): link the xQTL docs from the Download Pins menu once the resources page has content
    // TODO(EX-279): hide the view details button; the eQTL CT has no details page
    // TODO(EX-83): clicking a row's Pin or other controls should not select the row
  };

  constructor() {
    this.comparisonToolService.setViewConfig(this.viewConfig);
  }

  readonly pinnedDataEffect = effect(() => {
    if (this.platformService.isBrowser && this.isInitialized()) {
      const pinnedItems = this.comparisonToolService.pinnedItems();
      const sortMeta = this.comparisonToolService.multiSortMeta();
      this.getPinnedData(pinnedItems, sortMeta);
    }
  });

  readonly unpinnedDataEffect = effect(() => {
    if (this.platformService.isBrowser && this.isInitialized()) {
      const query = this.query();
      this.getUnpinnedData(query);
    }
  });

  ngOnInit() {
    if (this.platformService.isServer) {
      return;
    }

    this.comparisonToolService.connect({
      page: ComparisonToolPage.EQtls,
      config$: this.config$,
      queryParams$: this.comparisonToolUrlService.params$,
      pinAllFetch: (query, remainingBudget) =>
        this.eqtlService.getEqtls(this.buildUnpinnedQuery(query, { remainingBudget })).pipe(
          map((response) => ({
            rows: response.eqtls,
            totalElements: response.page.totalElements,
          })),
        ),
    });
  }

  ngOnDestroy() {
    this.comparisonToolService.disconnect();
  }

  private buildUnpinnedQuery(
    currentQuery: ComparisonToolQuery,
    options?: { remainingBudget?: number },
  ): EqtlSearchQuery {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(
      currentQuery.multiSortMeta,
    );

    const selectedFilters = this.comparisonToolService.selectedFilters();

    return {
      items: currentQuery.pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Exclude,
      ...this.comparisonToolService.buildPaginationOrBudget(currentQuery, options?.remainingBudget),
      sortFields,
      sortOrders,
      // TODO(EX-96): the Variant and Gene filter values are placeholders that match no rows until the search filter replaces them
      variant: selectedFilters['variant'],
      gene: selectedFilters['gene'],
      cellClass: selectedFilters['cellClass'],
      cellSubclass: selectedFilters['cellSubclass'],
    };
  }

  getUnpinnedData(currentQuery: ComparisonToolQuery) {
    const selectedRowId = this.comparisonToolService.selectedRowIdForUnpinnedFetch() ?? undefined;
    const query = { ...this.buildUnpinnedQuery(currentQuery), selectedRowId };

    this.logger.log('unpinned query', { query });

    this.comparisonToolService.fetchUnpinned(
      this.eqtlService.getEqtls(query).pipe(
        map((response: EqtlsPage) => ({
          data: response.eqtls,
          totalCount: response.page.totalElements,
          selectedRowLookup:
            selectedRowId !== undefined && response.selectedRowInResults != null
              ? { rowId: selectedRowId, inResults: response.selectedRowInResults }
              : undefined,
        })),
      ),
    );
  }

  getPinnedData(pinnedItems: string[], sortMeta: SortMeta[]) {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(sortMeta);

    const query: EqtlSearchQuery = {
      items: pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Include,
      sortFields,
      sortOrders,
    };

    this.logger.log('pinned query', { query });

    this.comparisonToolService.fetchPinned(
      this.eqtlService.getEqtls(query).pipe(
        map((response: EqtlsPage) => {
          const data = response.eqtls;
          return { data, totalCount: data.length };
        }),
      ),
    );
  }
}
