import { Component, DestroyRef, effect, inject, OnDestroy, OnInit } from '@angular/core';
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
  ComparisonToolUrlService,
  LoggerService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import {
  ComparisonToolConfigService,
  ComparisonToolPage,
  FoldChangeResult,
  ItemFilterTypeQuery,
  Transcriptomics,
  TranscriptomicsPage,
  TranscriptomicsSearchQuery,
  TranscriptomicsService,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { SortMeta } from 'primeng/api';
import { catchError, EMPTY, shareReplay } from 'rxjs';
import { DifferentialExpressionComparisonToolService } from './services/differential-expression-comparison-tool.service';

@Component({
  selector: 'model-ad-differential-expression-comparison-tool',
  imports: [ComparisonToolComponent],
  templateUrl: './differential-expression-comparison-tool.component.html',
  styleUrls: ['./differential-expression-comparison-tool.component.scss'],
})
export class DifferentialExpressionComparisonToolComponent implements OnInit, OnDestroy {
  private readonly platformService = inject(PlatformService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly transcriptomicsService = inject(TranscriptomicsService);
  private readonly comparisonToolService = inject(DifferentialExpressionComparisonToolService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);
  private readonly logger = inject(LoggerService);

  isInitialized = this.comparisonToolService.isInitialized;
  query = this.comparisonToolService.query;

  readonly config$ = this.comparisonToolConfigService
    .getComparisonToolConfig(ComparisonToolPage.DifferentialExpression)
    .pipe(
      catchError((error) => {
        this.logger.error('Error retrieving comparison tool config', error);
        return EMPTY;
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

  selectorsWikiParams: { [key: string]: SynapseWikiParams } = {
    'RNA - DIFFERENTIAL EXPRESSION': {
      ownerId: 'syn66271427',
      wikiId: '632873',
    },
  };

  legendPanelConfig: LegendPanelConfig = {
    colorChartLowerLabel: 'Downregulated',
    colorChartUpperLabel: 'Upregulated',
    colorChartText: `Circle color indicates the log2 fold change value. Red shades indicate reduced expression levels in AD patients compared to controls, while blue shades indicate increased expression levels in AD patients relative to controls.`,
    sizeChartLowerLabel: 'Significant',
    sizeChartUpperLabel: 'Insignificant',
    sizeChartText: `Circle diameter indicates P-value. Larger circles indicate higher statistical significance, while smaller circles indicate lower statistical significance.`,
  };

  viewConfig: Partial<ComparisonToolViewConfig> = {
    selectorsWikiParams: this.selectorsWikiParams,
    headerTitle: ComparisonToolPage.DifferentialExpression,
    filterResultsButtonTooltip: 'Filter results by Model, Biological Domain, and more',
    viewDetailsTooltip: 'View individual results',
    viewDetailsClick: (rowData: unknown) => {
      const row = rowData as Transcriptomics;
      const url = this.router.serializeUrl(
        this.router.createUrlTree([ROUTE_PATHS.GENES, row.ensembl_gene_id], {
          queryParams:
            row.model_group === null
              ? { model: row.name.link_text, tissue: row.tissue }
              : { modelGroup: row.model_group, tissue: row.tissue },
        }),
      );
      window.open(url, '_blank');
    },
    legendPanelConfig: this.legendPanelConfig,
    rowIdDataKey: 'composite_id',
    defaultSort: [
      { field: 'gene_symbol', order: 1 },
      { field: 'name', order: 1 },
    ],
    heatmapCircleClickTransformFn: ({
      rowData,
      cellData,
      columnKey,
    }: HeatmapCircleClickTransformFnContext) => {
      const row = rowData as Transcriptomics;
      const cell = cellData as FoldChangeResult;
      return {
        label: row.gene_symbol
          ? { left: row.gene_symbol, right: row.ensembl_gene_id }
          : { left: row.ensembl_gene_id },
        heading: `Differential RNA Expression (${row.tissue})`,
        subHeadings: [
          `${row.name} (${columnKey}, ${row.sex_cohort})`,
          `Matched Control: ${row.matched_control}`,
        ],
        value: cell.log2_fc,
        valueLabel: 'Log 2 Fold Change',
        pValue: cell.adj_p_val,
        footer: 'Significance is considered to be an adjusted p-value < 0.05',
      };
    },
    linkExportField: 'link_text',
  };

  constructor() {
    this.comparisonToolService.setViewConfig(this.viewConfig);
  }

  // Effect for pinned data - only re-fetch when pinnedItems, categories, or sort change
  readonly pinnedDataEffect = effect(() => {
    if (this.platformService.isBrowser && this.isInitialized()) {
      const categories = this.comparisonToolService.dropdownSelection();
      const pinnedItems = this.comparisonToolService.pinnedItems();
      const sortMeta = this.comparisonToolService.multiSortMeta();
      this.getPinnedData(categories, pinnedItems, sortMeta);
    }
  });

  // Effect for unpinned data - re-fetch when any query parameter changes
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

    const query: TranscriptomicsSearchQuery = {
      categories: currentQuery.categories,
      items: currentQuery.pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Exclude,
      pageNumber: currentQuery.pageNumber,
      pageSize: currentQuery.pageSize,
      search: currentQuery.searchTerm,
      biodomains: selectedFilters['biodomains'],
      modelType: selectedFilters['modelTypes'],
      name: selectedFilters['models'],
      sortFields,
      sortOrders,
    };

    this.comparisonToolService.startFetch();
    this.logger.log(
      `TranscriptomicsComparisonToolComponent: unpinned query ${JSON.stringify(query)}`,
    );

    this.transcriptomicsService
      .getTranscriptomics(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: TranscriptomicsPage) => {
          const data = response.transcriptomics.map((row) =>
            row.model_group
              ? { ...row, name: { ...row.name, link_url: `models/${row.model_group}` } }
              : row,
          );
          this.comparisonToolService.setUnpinnedData(data);
          this.comparisonToolService.totalResultsCount.set(response.page.totalElements);
        },
        error: () => {
          this.comparisonToolService.setUnpinnedData([]);
          this.comparisonToolService.totalResultsCount.set(0);
        },
      });
  }

  getPinnedData(categories: string[], pinnedItems: string[], sortMeta: SortMeta[]) {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(sortMeta);

    const query: TranscriptomicsSearchQuery = {
      categories,
      items: pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Include,
      sortFields,
      sortOrders,
    };

    this.comparisonToolService.startFetch();
    this.logger.log(
      `DifferentialExpressionComparisonToolComponent: pinned query ${JSON.stringify(query)}`,
    );

    this.transcriptomicsService
      .getTranscriptomics(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: TranscriptomicsPage) => {
          const data = response.transcriptomics.map((row) =>
            row.model_group
              ? { ...row, name: { ...row.name, link_url: `models/${row.model_group}` } }
              : row,
          );
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
