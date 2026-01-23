import { Component, DestroyRef, OnDestroy, OnInit, effect, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  ComparisonToolQuery,
  ComparisonToolViewConfig,
  HeatmapCircleClickTransformFnContext,
  LegendPanelConfig,
  SynapseWikiParams,
} from '@sagebionetworks/explorers/models';
import {
  ComparisonToolHelperService,
  ComparisonToolUrlService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import {
  ComparisonToolConfig,
  ComparisonToolConfigService,
  ComparisonToolPage,
  CorrelationResult,
  DiseaseCorrelation,
  DiseaseCorrelationSearchQuery,
  DiseaseCorrelationService,
  DiseaseCorrelationsPage,
  ItemFilterTypeQuery,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { SortMeta } from 'primeng/api';
import { catchError, of, shareReplay } from 'rxjs';
import { DiseaseCorrelationComparisonToolService } from './services/disease-correlation-comparison-tool.service';

@Component({
  selector: 'model-ad-disease-correlation-comparison-tool',
  imports: [ComparisonToolComponent],
  templateUrl: './disease-correlation-comparison-tool.component.html',
  styleUrls: ['./disease-correlation-comparison-tool.component.scss'],
})
export class DiseaseCorrelationComparisonToolComponent implements OnInit, OnDestroy {
  private readonly platformService = inject(PlatformService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly comparisonToolHelperService = inject(ComparisonToolHelperService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly diseaseCorrelationService = inject(DiseaseCorrelationService);
  private readonly comparisonToolService = inject(DiseaseCorrelationComparisonToolService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);

  isInitialized = this.comparisonToolService.isInitialized;
  query = this.comparisonToolService.query;

  readonly config$ = this.comparisonToolConfigService
    .getComparisonToolConfig(ComparisonToolPage.DiseaseCorrelation)
    .pipe(
      catchError((error) => {
        console.error('Error retrieving comparison tool config: ', error);
        this.router.navigateByUrl(ROUTE_PATHS.ERROR, { skipLocationChange: true });
        return of<ComparisonToolConfig[]>([]);
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

  selectorsWikiParams: { [key: string]: SynapseWikiParams } = {
    'CONSENSUS NETWORK MODULES': {
      ownerId: 'syn66271427',
      wikiId: '632874',
    },
  };

  legendPanelConfig: LegendPanelConfig = {
    colorChartLowerLabel: 'Negative Correlation',
    colorChartUpperLabel: 'Positive Correlation',
    colorChartText: `Circle color indicates the correlation between changes in expression of gene modules in the mouse model versus in humans with AD. Red shades indicate a negative correlation, while blue shades indicate a positive correlation.`,
    sizeChartLowerLabel: 'Significant',
    sizeChartUpperLabel: 'Insignificant',
    sizeChartText: `Circle diameter indicates P-value. Larger circles indicate higher statistical significance, while smaller circles indicate lower statistical significance.`,
  };

  viewConfig: Partial<ComparisonToolViewConfig> = {
    selectorsWikiParams: this.selectorsWikiParams,
    headerTitle: ComparisonToolPage.DiseaseCorrelation,
    filterResultsButtonTooltip: 'Filter results by Age, Sex, Modified Gene, and more',
    viewDetailsTooltip: 'Open model details page',
    viewDetailsClick: (rowData: unknown) => {
      const data = rowData as DiseaseCorrelation;
      const url = this.router.serializeUrl(
        this.router.createUrlTree([ROUTE_PATHS.MODELS, data.name]),
      );
      window.open(url, '_blank');
    },
    legendPanelConfig: this.legendPanelConfig,
    rowsPerPage: 10,
    rowIdDataKey: 'composite_id',
    defaultSort: [
      { field: 'name', order: 1 },
      { field: 'age', order: 1 },
      { field: 'sex', order: 1 },
    ],
    heatmapCircleClickTransformFn: ({
      rowData,
      cellData,
      columnKey,
    }: HeatmapCircleClickTransformFnContext) => {
      const row = rowData as DiseaseCorrelation;
      const cell = cellData as CorrelationResult;
      return {
        heading: 'Disease Correlation (Consensus Network Modules)',
        subHeadings: [
          `${row.name} (${row.age}, ${row.sex})`,
          `${row.cluster} | ${columnKey} module`,
        ],
        value: cell.correlation,
        valueLabel: 'Correlation',
        pValue: cell.adj_p_val,
        footer: 'Significance is considered to be an adjusted p-value < 0.05',
      };
    },
  };

  constructor() {
    this.comparisonToolService.setViewConfig(this.viewConfig);
  }

  readonly pinnedDataEffect = effect(() => {
    if (this.platformService.isBrowser && this.isInitialized()) {
      const categories = this.comparisonToolService.dropdownSelection();
      const pinnedItems = this.comparisonToolService.pinnedItems();
      const sortMeta = this.comparisonToolService.multiSortMeta();
      this.getPinnedData(categories, pinnedItems, sortMeta);
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

    const query: DiseaseCorrelationSearchQuery = {
      categories: currentQuery.categories,
      items: currentQuery.pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Exclude,
      pageNumber: currentQuery.pageNumber,
      pageSize: currentQuery.pageSize,
      search: currentQuery.searchTerm,
      age: selectedFilters['age'],
      modelType: selectedFilters['model_type'],
      modifiedGenes: selectedFilters['modified_genes'],
      name: selectedFilters['name'],
      sex: selectedFilters['sex'],
      sortFields,
      sortOrders,
    };

    this.diseaseCorrelationService
      .getDiseaseCorrelations(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: DiseaseCorrelationsPage) => {
          const data = response.diseaseCorrelations;
          this.comparisonToolService.setUnpinnedData(data);
          this.comparisonToolService.totalResultsCount.set(response.page.totalElements);
        },
        error: (error) => {
          console.error('Error in getUnpinnedData:', error);
          throw new Error('Error fetching disease correlation data:', { cause: error });
        },
      });
  }

  getPinnedData(categories: string[], pinnedItems: string[], sortMeta: SortMeta[]) {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(sortMeta);

    const query: DiseaseCorrelationSearchQuery = {
      categories,
      items: pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Include,
      sortFields,
      sortOrders,
    };

    this.diseaseCorrelationService
      .getDiseaseCorrelations(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: DiseaseCorrelationsPage) => {
          const data = response.diseaseCorrelations;
          this.comparisonToolService.setPinnedData(data);
          this.comparisonToolService.pinnedResultsCount.set(data.length);
        },
        error: (error) => {
          throw new Error('Error fetching disease correlation data:', { cause: error });
        },
      });
  }
}
