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
import { map, of, shareReplay } from 'rxjs';
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
    defaultSort: [{ field: 'adj_p_val', order: 1 }],
    // TODO(EX-251): set the row id and pinned download
    // TODO(EX-287): round the Beta and MAF columns on screen
    // TODO(EX-252): turn on row selection and hover
  };

  constructor() {
    this.comparisonToolService.setViewConfig(this.viewConfig);
  }

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
      // TODO(EX-251): fetch the matching rows for Pin All
      pinAllFetch: () => of({ rows: [], totalElements: 0 }),
    });
  }

  ngOnDestroy() {
    this.comparisonToolService.disconnect();
  }

  private buildUnpinnedQuery(currentQuery: ComparisonToolQuery): EqtlSearchQuery {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(
      currentQuery.multiSortMeta,
    );

    const selectedFilters = this.comparisonToolService.selectedFilters();

    // TODO(EX-251): send the pinned items
    return {
      itemFilterType: ItemFilterTypeQuery.Exclude,
      ...this.comparisonToolService.buildPaginationOrBudget(currentQuery),
      sortFields,
      sortOrders,
      variant: selectedFilters['variant'],
      gene: selectedFilters['gene'],
      cellClass: selectedFilters['cellClass'],
      cellSubclass: selectedFilters['cellSubclass'],
    };
  }

  getUnpinnedData(currentQuery: ComparisonToolQuery) {
    const query = this.buildUnpinnedQuery(currentQuery);

    this.logger.log('unpinned query', { query });

    this.comparisonToolService.fetchUnpinned(
      this.eqtlService.getEqtls(query).pipe(
        map((response: EqtlsPage) => ({
          data: response.eqtls,
          totalCount: response.page.totalElements,
        })),
      ),
    );
  }

  // TODO(EX-251): fetch the pinned rows; the URL isn't updated until the first pinned fetch lands
}
