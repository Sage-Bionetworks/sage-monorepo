import { Component, computed, DestroyRef, effect, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  ComparisonToolQuery,
  ComparisonToolViewConfig,
  HeatmapCircleClickTransformFnContext,
  HeatmapDetailsPanelData,
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
  NamedLink,
  PageMetadata,
  Proteomics,
  ProteomicsSearchQuery,
  ProteomicsService,
  Transcriptomics,
  TranscriptomicsSearchQuery,
  TranscriptomicsService,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { SortMeta } from 'primeng/api';
import { catchError, EMPTY, map, Observable, shareReplay } from 'rxjs';
import { PROTEIN_MAIN_CATEGORY, RNA_MAIN_CATEGORY } from './differential-expression-categories';
import {
  DifferentialExpressionComparisonToolService,
  DifferentialExpressionRow,
} from './services/differential-expression-comparison-tool.service';

type DifferentialExpressionSearchQuery = TranscriptomicsSearchQuery & ProteomicsSearchQuery;

interface DifferentialExpressionPage {
  rows: DifferentialExpressionRow[];
  page: PageMetadata;
}

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
  private readonly proteomicsService = inject(ProteomicsService);
  private readonly comparisonToolService = inject(DifferentialExpressionComparisonToolService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);
  private readonly logger = inject(LoggerService);

  isInitialized = this.comparisonToolService.isInitialized;
  query = this.comparisonToolService.query;

  private readonly mainCategory = computed<string | undefined>(
    () => this.comparisonToolService.dropdownSelection()[0],
  );

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
    [RNA_MAIN_CATEGORY]: {
      ownerId: 'syn66271427',
      wikiId: '632873',
    },
    [PROTEIN_MAIN_CATEGORY]: {
      ownerId: 'syn66271427',
      wikiId: '643119',
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
      const mainCategory = this.mainCategory();
      switch (mainCategory) {
        case RNA_MAIN_CATEGORY: {
          const row = rowData as Transcriptomics;
          this.openDetails([ROUTE_PATHS.GENES, row.ensembl_gene_id], row);
          break;
        }
        case PROTEIN_MAIN_CATEGORY: {
          const row = rowData as Proteomics;
          this.openDetails([ROUTE_PATHS.PROTEINS, row.unique_id], row);
          break;
        }
        default:
          this.logUnrecognizedMainCategory(mainCategory);
      }
    },
    legendPanelConfig: this.legendPanelConfig,
    rowIdDataKey: 'composite_id',
    defaultSort: [
      { field: 'gene_symbol', order: 1 },
      { field: 'name', order: 1 },
      { field: 'sex', order: 1 },
    ],
    heatmapCircleClickTransformFn: ({
      rowData,
      cellData,
      columnKey,
    }: HeatmapCircleClickTransformFnContext) => {
      const cell = cellData as FoldChangeResult;
      const mainCategory = this.mainCategory();
      switch (mainCategory) {
        case RNA_MAIN_CATEGORY: {
          const row = rowData as Transcriptomics;
          return this.buildHeatmapDetailsPanelData(row, cell, columnKey, {
            label: row.gene_symbol
              ? { left: row.gene_symbol, right: row.ensembl_gene_id }
              : { left: row.ensembl_gene_id },
            heading: `Differential RNA Expression (${row.tissue})`,
          });
        }
        case PROTEIN_MAIN_CATEGORY: {
          const row = rowData as Proteomics;
          return this.buildHeatmapDetailsPanelData(row, cell, columnKey, {
            label: row.gene_symbol
              ? { left: row.display_symbol, right: row.ensembl_gene_id }
              : { left: row.display_symbol },
            heading: `Differential Protein Expression (${row.tissue})`,
          });
        }
        default:
          this.logUnrecognizedMainCategory(mainCategory);
          return null;
      }
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

    const query: DifferentialExpressionSearchQuery = {
      categories: currentQuery.categories,
      items: currentQuery.pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Exclude,
      pageNumber: currentQuery.pageNumber,
      pageSize: currentQuery.pageSize,
      search: currentQuery.searchTerm,
      biodomains: selectedFilters['biodomains'],
      modelType: selectedFilters['modelTypes'],
      name: selectedFilters['models'],
      sex: selectedFilters['sexes'],
      sortFields,
      sortOrders,
    };

    this.comparisonToolService.startFetch();
    this.logger.log(
      `DifferentialExpressionComparisonToolComponent: unpinned query ${JSON.stringify(query)}`,
    );

    const mainCategory = currentQuery.categories[0];
    const page$ = this.fetchDifferentialExpressionPage(mainCategory, query);
    if (page$ === null) {
      this.logUnrecognizedMainCategory(mainCategory);
      this.comparisonToolService.setUnpinnedData([]);
      this.comparisonToolService.totalResultsCount.set(0);
      return;
    }

    page$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: ({ rows, page }: DifferentialExpressionPage) => {
        this.comparisonToolService.setUnpinnedData(this.applyModelGroupLink(rows));
        this.comparisonToolService.totalResultsCount.set(page.totalElements);
      },
      error: () => {
        this.comparisonToolService.setUnpinnedData([]);
        this.comparisonToolService.totalResultsCount.set(0);
      },
    });
  }

  getPinnedData(categories: string[], pinnedItems: string[], sortMeta: SortMeta[]) {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(sortMeta);

    const query: DifferentialExpressionSearchQuery = {
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

    const mainCategory = categories[0];
    const page$ = this.fetchDifferentialExpressionPage(mainCategory, query);
    if (page$ === null) {
      this.logUnrecognizedMainCategory(mainCategory);
      this.comparisonToolService.setPinnedData([]);
      this.comparisonToolService.pinnedResultsCount.set(0);
      return;
    }

    page$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: ({ rows }: DifferentialExpressionPage) => {
        const data = this.applyModelGroupLink(rows);
        this.comparisonToolService.setPinnedData(data);
        this.comparisonToolService.pinnedResultsCount.set(data.length);
      },
      error: () => {
        this.comparisonToolService.setPinnedData([]);
        this.comparisonToolService.pinnedResultsCount.set(0);
      },
    });
  }

  private fetchDifferentialExpressionPage(
    mainCategory: string | undefined,
    query: DifferentialExpressionSearchQuery,
  ): Observable<DifferentialExpressionPage> | null {
    switch (mainCategory) {
      case RNA_MAIN_CATEGORY:
        return this.transcriptomicsService
          .getTranscriptomics(query)
          .pipe(map((response) => ({ rows: response.transcriptomics, page: response.page })));
      case PROTEIN_MAIN_CATEGORY:
        return this.proteomicsService
          .getProteomics(query)
          .pipe(map((response) => ({ rows: response.proteomics, page: response.page })));
      default:
        return null;
    }
  }

  private applyModelGroupLink<T extends { name: NamedLink; model_group: string | null }>(
    rows: T[],
  ): T[] {
    return rows.map((row) =>
      row.model_group
        ? { ...row, name: { ...row.name, link_url: `models/${row.model_group}` } }
        : row,
    );
  }

  private openDetails(commands: string[], row: DifferentialExpressionRow) {
    const url = this.router.serializeUrl(
      this.router.createUrlTree(commands, {
        queryParams:
          row.model_group === null
            ? { model: row.name.link_text, tissue: row.tissue }
            : { modelGroup: row.model_group, tissue: row.tissue },
      }),
    );
    window.open(url, '_blank');
  }

  private buildHeatmapDetailsPanelData(
    row: DifferentialExpressionRow,
    cell: FoldChangeResult,
    columnKey: string,
    modalityDetails: Pick<HeatmapDetailsPanelData, 'label' | 'heading'>,
  ): HeatmapDetailsPanelData {
    return {
      ...modalityDetails,
      subHeadings: [
        `${row.name.link_text} (${columnKey}, ${row.sex})`,
        `Matched Control: ${row.matched_control}`,
      ],
      value: cell.log2_fc,
      valueLabel: 'Log 2 Fold Change',
      pValue: cell.adj_p_val,
      footer: 'Significance is considered to be an adjusted p-value < 0.05',
    };
  }

  private logUnrecognizedMainCategory(mainCategory: string | undefined) {
    this.logger.error(
      `DifferentialExpressionComparisonToolComponent: unrecognized main category '${mainCategory}'`,
    );
  }
}
