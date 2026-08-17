import { Component, DestroyRef, effect, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
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
  MarmosetModelOverview,
  MarmosetModelOverviewSearchQuery,
  MarmosetModelOverviewService,
  MarmosetModelOverviewsPage,
  ModelOrganism,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { SortMeta } from 'primeng/api';
import { catchError, EMPTY, shareReplay } from 'rxjs';
import { MarmosetModelOverviewComparisonToolService } from './services/marmoset-model-overview-comparison-tool.service';

@Component({
  selector: 'model-ad-marmoset-model-overview-comparison-tool',
  imports: [ComparisonToolComponent],
  templateUrl: './marmoset-model-overview-comparison-tool.component.html',
  styleUrls: ['./marmoset-model-overview-comparison-tool.component.scss'],
})
export class MarmosetModelOverviewComparisonToolComponent implements OnInit, OnDestroy {
  private readonly platformService = inject(PlatformService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly marmosetModelOverviewService = inject(MarmosetModelOverviewService);
  private readonly comparisonToolService = inject(MarmosetModelOverviewComparisonToolService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);
  private readonly logger = inject(LoggerService);

  isInitialized = this.comparisonToolService.isInitialized;
  query = this.comparisonToolService.query;

  readonly config$ = this.comparisonToolConfigService
    .getComparisonToolConfig(ComparisonToolPage.MarmosetModelOverview)
    .pipe(
      catchError((error) => {
        this.logger.error('Error retrieving comparison tool config', error);
        return EMPTY;
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

  viewConfig: Partial<ComparisonToolViewConfig> = {
    headerTitle: ComparisonToolPage.MarmosetModelOverview,
    filterResultsButtonTooltip: 'Filter results by Model Type, Modified Gene, and more',
    showSignificanceControls: false,
    viewDetailsTooltip: 'Open model details page',
    viewDetailsClick: (rowData: unknown) => {
      const data = rowData as MarmosetModelOverview;
      const url = this.router.serializeUrl(
        this.router.createUrlTree([ROUTE_PATHS.MODELS, data.name], {
          queryParams: { modelOrganism: ModelOrganism.Marmoset },
        }),
      );
      window.open(url, '_blank');
    },
    legendEnabled: false,
    rowIdDataKey: 'name',
    allowPinnedImageDownload: false,
    defaultSort: [{ field: 'name', order: 1 }],
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
    });
  }

  ngOnDestroy() {
    this.comparisonToolService.disconnect();
  }

  getUnpinnedData(currentQuery: ComparisonToolQuery) {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(
      currentQuery.multiSortMeta,
    );

    const selectedFilters = this.comparisonToolService.selectedFilters();

    const query: MarmosetModelOverviewSearchQuery = {
      items: currentQuery.pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Exclude,
      pageNumber: currentQuery.pageNumber,
      pageSize: currentQuery.pageSize,
      search: currentQuery.searchTerm,
      sortFields,
      sortOrders,
      availableData: selectedFilters['availableData'],
      modelTypes: selectedFilters['modelTypes'],
      modifiedGenes: selectedFilters['modifiedGenes'],
    };
    this.comparisonToolService.startFetch();
    this.logger.log(
      `MarmosetModelOverviewComparisonToolComponent: unpinned query ${JSON.stringify(query)}`,
    );

    this.marmosetModelOverviewService
      .getMarmosetModelOverviews(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: MarmosetModelOverviewsPage) => {
          const data = response.marmosetModelOverviews;
          this.comparisonToolService.setUnpinnedData(data);
          this.comparisonToolService.totalResultsCount.set(response.page.totalElements);
        },
        error: () => {
          this.comparisonToolService.setUnpinnedData([]);
          this.comparisonToolService.totalResultsCount.set(0);
        },
      });
  }

  getPinnedData(pinnedItems: string[], sortMeta: SortMeta[]) {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(sortMeta);

    const query: MarmosetModelOverviewSearchQuery = {
      items: pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Include,
      sortFields,
      sortOrders,
    };

    this.comparisonToolService.startFetch();
    this.logger.log(
      `MarmosetModelOverviewComparisonToolComponent: pinned query ${JSON.stringify(query)}`,
    );

    this.marmosetModelOverviewService
      .getMarmosetModelOverviews(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: MarmosetModelOverviewsPage) => {
          const data = response.marmosetModelOverviews;
          this.comparisonToolService.setPinnedData(data);
          this.comparisonToolService.pinnedResultsCount.set(data.length);
        },
        error: () => {
          this.comparisonToolService.setPinnedData([]);
          this.comparisonToolService.pinnedResultsCount.set(0);
        },
      });
  }
}
