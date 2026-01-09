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

  // TODO MG-485 - Update overview panes content and images
  visualizationOverviewPanes = [
    this.comparisonToolHelperService.createVisualizationOverviewPane(
      ComparisonToolPage.ModelOverview,
      `<p>Welcome to Agora's Model Overview Tool. This overview demonstrates how to use the tool to explore results about models related to AD. You can revisit this walkthrough by clicking the Visualization Overview link at the bottom of the page.</p>
      <p>Click on the Legend link at the bottom of the page to view the legend for the current visualization.</p>
      <img src="/explorer-assets/images/gct-how-to-0.svg" />`,
    ),
    this.comparisonToolHelperService.createVisualizationOverviewPane(
      'View Detailed Expression Info',
      `<p>Click on a circle to show detailed information about a result for a specific brain region.</p>
      <img src="/explorer-assets/images/gct-how-to-1.gif" />`,
    ),
    this.comparisonToolHelperService.createVisualizationOverviewPane(
      'Compare Multiple Genes',
      `<p>You can pin several genes to visually compare them together. Then export the data about your pinned genes as a CSV file for further analysis.</p>
      <img src="/explorer-assets/images/gct-how-to-2.gif" />`,
    ),
    this.comparisonToolHelperService.createVisualizationOverviewPane(
      'Filter Gene Selection',
      `<p>Filter genes by Nomination, Association with AD, Study and more. Or simply use the search bar to quickly find the genes you are interested in.</p>
      <img src="/explorer-assets/images/gct-how-to-3.gif" />`,
    ),
  ];

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
    visualizationOverviewPanes: this.visualizationOverviewPanes,
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
      const pinnedItems = this.query().pinnedItems;
      const sortMeta = this.query().multiSortMeta;
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

    const selectedFilters = this.comparisonToolHelperService.getSelectedFilters(
      currentQuery.filters,
    );

    const query: ModelOverviewSearchQuery = {
      items: currentQuery.pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Exclude,
      pageNumber: currentQuery.pageNumber,
      pageSize: currentQuery.pageSize,
      search: currentQuery.searchTerm,
      sortFields,
      sortOrders,
      availableData: selectedFilters['available_data'],
      center: selectedFilters['center'],
      modelType: selectedFilters['model_type'],
      modifiedGenes: selectedFilters['modified_genes'],
    };
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
