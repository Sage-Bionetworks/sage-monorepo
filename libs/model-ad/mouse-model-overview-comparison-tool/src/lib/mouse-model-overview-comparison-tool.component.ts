import { Component, effect, inject, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
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
  ItemFilterTypeQuery,
  ModelOrganism,
  MouseModelOverview,
  MouseModelOverviewSearchQuery,
  MouseModelOverviewService,
  MouseModelOverviewsPage,
} from '@sagebionetworks/model-ad/api-client';
import { DOWNLOAD_PINS_NOTE, ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { SortMeta } from 'primeng/api';
import { catchError, EMPTY, map, shareReplay } from 'rxjs';
import { MouseModelOverviewComparisonToolService } from './services/mouse-model-overview-comparison-tool.service';

@Component({
  selector: 'model-ad-mouse-model-overview-comparison-tool',
  imports: [ComparisonToolComponent],
  templateUrl: './mouse-model-overview-comparison-tool.component.html',
  styleUrls: ['./mouse-model-overview-comparison-tool.component.scss'],
})
export class MouseModelOverviewComparisonToolComponent implements OnInit, OnDestroy {
  private readonly platformService = inject(PlatformService);
  private readonly router = inject(Router);
  private readonly mouseModelOverviewService = inject(MouseModelOverviewService);
  private readonly comparisonToolService = inject(MouseModelOverviewComparisonToolService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);
  private readonly logger = inject(LoggerService);

  isInitialized = this.comparisonToolService.isInitialized;
  query = this.comparisonToolService.query;

  readonly config$ = this.comparisonToolConfigService
    .getComparisonToolConfig(ComparisonToolPage.MouseModelOverview)
    .pipe(
      catchError((error) => {
        this.logger.error('Error retrieving comparison tool config', error);
        return EMPTY;
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

  viewConfig: Partial<ComparisonToolViewConfig> = {
    headerTitle: ComparisonToolPage.MouseModelOverview,
    filterResultsButtonTooltip: 'Filter results by Model Type, Modified Gene, and more',
    showSignificanceControls: false,
    viewDetailsTooltip: 'Open model details page',
    viewDetailsClick: (rowData: unknown) => {
      const data = rowData as MouseModelOverview;
      const url = this.router.serializeUrl(
        this.router.createUrlTree([ROUTE_PATHS.MODELS, data.name], {
          queryParams: { modelOrganism: ModelOrganism.Mouse },
        }),
      );
      window.open(url, '_blank');
    },
    legendEnabled: false,
    rowIdDataKey: 'name',
    allowPinnedImageDownload: false,
    downloadPinsNote: DOWNLOAD_PINS_NOTE,
    defaultSort: [
      { field: 'model_type', order: -1 },
      { field: 'name', order: 1 },
    ],
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
      config$: this.config$,
      queryParams$: this.comparisonToolUrlService.params$,
      pinAllFetch: (query, remainingBudget) =>
        this.mouseModelOverviewService
          .getMouseModelOverviews(this.buildUnpinnedQuery(query, { remainingBudget }))
          .pipe(
            map((response) => ({
              rows: response.mouseModelOverviews,
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
  ): MouseModelOverviewSearchQuery {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(
      currentQuery.multiSortMeta,
    );

    const selectedFilters = this.comparisonToolService.selectedFilters();

    return {
      items: currentQuery.pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Exclude,
      ...this.comparisonToolService.buildPaginationOrBudget(currentQuery, options?.remainingBudget),
      search: currentQuery.searchTerm,
      sortFields,
      sortOrders,
      availableData: selectedFilters['availableData'],
      center: selectedFilters['centers'],
      modelType: selectedFilters['modelTypes'],
      modifiedGenes: selectedFilters['modifiedGenes'],
    };
  }

  getUnpinnedData(currentQuery: ComparisonToolQuery) {
    const query = this.buildUnpinnedQuery(currentQuery);

    this.logger.log(
      `MouseModelOverviewComparisonToolComponent: unpinned query ${JSON.stringify(query)}`,
    );

    this.comparisonToolService.fetchUnpinned(
      this.mouseModelOverviewService.getMouseModelOverviews(query).pipe(
        map((response: MouseModelOverviewsPage) => ({
          data: response.mouseModelOverviews,
          totalCount: response.page.totalElements,
        })),
      ),
    );
  }

  getPinnedData(pinnedItems: string[], sortMeta: SortMeta[]) {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(sortMeta);

    const query: MouseModelOverviewSearchQuery = {
      items: pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Include,
      sortFields,
      sortOrders,
    };

    this.logger.log(
      `MouseModelOverviewComparisonToolComponent: pinned query ${JSON.stringify(query)}`,
    );

    this.comparisonToolService.fetchPinned(
      this.mouseModelOverviewService.getMouseModelOverviews(query).pipe(
        map((response: MouseModelOverviewsPage) => {
          const data = response.mouseModelOverviews;
          return { data, totalCount: data.length };
        }),
      ),
    );
  }
}
