import { Component, DestroyRef, effect, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import { ComparisonToolQuery, ComparisonToolViewConfig } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolHelperService,
  ComparisonToolUrlService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import {
  ComparisonToolConfig,
  ComparisonToolConfigService,
  ComparisonToolPage,
  ItemFilterTypeQuery,
  ModelOverview,
  ModelOverviewSearchQuery,
  ModelOverviewService,
  ModelOverviewsPage,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { SortMeta } from 'primeng/api';
import { catchError, of, shareReplay } from 'rxjs';
import { ModelOverviewComparisonToolService } from './services/model-overview-comparison-tool.service';

@Component({
  selector: 'model-ad-model-overview-comparison-tool',
  imports: [ComparisonToolComponent],
  templateUrl: './model-overview-comparison-tool.component.html',
  styleUrls: ['./model-overview-comparison-tool.component.scss'],
})
export class ModelOverviewComparisonToolComponent implements OnInit, OnDestroy {
  private readonly platformService = inject(PlatformService);
  private readonly comparisonToolHelperService = inject(ComparisonToolHelperService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly modelOverviewService = inject(ModelOverviewService);
  private readonly comparisonToolService = inject(ModelOverviewComparisonToolService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);

  isInitialized = this.comparisonToolService.isInitialized;
  query = this.comparisonToolService.query;

  readonly config$ = this.comparisonToolConfigService
    .getComparisonToolConfig(ComparisonToolPage.ModelOverview)
    .pipe(
      catchError((error) => {
        console.error('Error retrieving comparison tool config: ', error);
        this.router.navigateByUrl(ROUTE_PATHS.ERROR, { skipLocationChange: true });
        return of<ComparisonToolConfig[]>([]);
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

  viewConfig: Partial<ComparisonToolViewConfig> = {
    headerTitle: ComparisonToolPage.ModelOverview,
    filterResultsButtonTooltip: 'Filter results by Model Type, Modified Gene, and more',
    showSignificanceControls: false,
    viewDetailsTooltip: 'Open model details page',
    viewDetailsClick: (rowData: unknown) => {
      const data = rowData as ModelOverview;
      const url = this.router.serializeUrl(
        this.router.createUrlTree([ROUTE_PATHS.MODELS, data.name]),
      );
      window.open(url, '_blank');
    },
    legendEnabled: false,
    rowsPerPage: 10,
    rowIdDataKey: 'name',
    allowPinnedImageDownload: false,
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

    const query: ModelOverviewSearchQuery = {
      items: currentQuery.pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Exclude,
      pageNumber: currentQuery.pageNumber,
      pageSize: currentQuery.pageSize,
      search: currentQuery.searchTerm,
      sortFields,
      sortOrders,
      availableData: selectedFilters['availableData'],
      center: selectedFilters['centers'],
      modelType: selectedFilters['modelTypes'],
      modifiedGenes: selectedFilters['modifiedGenes'],
    };
    this.comparisonToolService.startFetch();
    this.modelOverviewService
      .getModelOverviews(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: ModelOverviewsPage) => {
          const data = response.modelOverviews;
          this.comparisonToolService.setUnpinnedData(data);
          this.comparisonToolService.totalResultsCount.set(response.page.totalElements);
        },
        error: (error) => {
          throw new Error('Error fetching model overview data:', { cause: error });
        },
      });
  }

  getPinnedData(pinnedItems: string[], sortMeta: SortMeta[]) {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(sortMeta);

    const query: ModelOverviewSearchQuery = {
      items: pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Include,
      sortFields,
      sortOrders,
    };

    this.comparisonToolService.startFetch();
    this.modelOverviewService
      .getModelOverviews(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: ModelOverviewsPage) => {
          const data = response.modelOverviews;
          this.comparisonToolService.setPinnedData(data);
          this.comparisonToolService.pinnedResultsCount.set(data.length);
        },
        error: (error) => {
          throw new Error('Error fetching model overview data:', { cause: error });
        },
      });
  }
}
