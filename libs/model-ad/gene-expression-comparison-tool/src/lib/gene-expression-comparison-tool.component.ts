import { Component, DestroyRef, effect, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  ComparisonToolViewConfig,
  LegendPanelConfig,
  SynapseWikiParams,
} from '@sagebionetworks/explorers/models';
import {
  ComparisonToolFilterService,
  ComparisonToolHelperService,
  ComparisonToolUrlService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import {
  ComparisonToolConfig,
  ComparisonToolConfigService,
  ComparisonToolPage,
  GeneExpressionSearchQuery,
  GeneExpressionService,
  GeneExpressionsPage,
  ItemFilterTypeQuery,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { catchError, of, shareReplay } from 'rxjs';
import { GeneExpressionComparisonToolService } from './services/gene-expression-comparison-tool.service';

@Component({
  selector: 'model-ad-gene-expression-comparison-tool',
  imports: [ComparisonToolComponent],
  templateUrl: './gene-expression-comparison-tool.component.html',
  styleUrls: ['./gene-expression-comparison-tool.component.scss'],
})
export class GeneExpressionComparisonToolComponent implements OnInit, OnDestroy {
  private readonly platformService = inject(PlatformService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly comparisonToolHelperService = inject(ComparisonToolHelperService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly geneExpressionService = inject(GeneExpressionService);
  private readonly comparisonToolService = inject(GeneExpressionComparisonToolService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);
  private readonly comparisonToolFilterService = inject(ComparisonToolFilterService);

  pinnedItems = this.comparisonToolService.pinnedItems;
  isInitialized = this.comparisonToolService.isInitialized;

  currentPageNumber = this.comparisonToolService.pageNumber;
  currentPageSize = this.comparisonToolService.pageSize;
  searchTerm = this.comparisonToolFilterService.searchTerm;

  readonly config$ = this.comparisonToolConfigService
    .getComparisonToolConfig(ComparisonToolPage.GeneExpression)
    .pipe(
      catchError((error) => {
        console.error('Error retrieving comparison tool config: ', error);
        this.router.navigateByUrl(ROUTE_PATHS.ERROR, { skipLocationChange: true });
        return of<ComparisonToolConfig[]>([]);
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
    colorChartText: `Circle color indicates the log2 fold change value. Red shades indicate reduced expression levels in AD patients compared  to controls, while blue shades indicate increased expression levels in AD patients relative to controls.`,
    sizeChartLowerLabel: 'Significant',
    sizeChartUpperLabel: 'Insignificant',
    sizeChartText: `Circle diameter indicates P-value. Larger circles indicate higher statistical significance, while smaller circles indicate lower statistical significance.`,
  };

  // TODO MG-485 - Update overview panes content and images
  visualizationOverviewPanes = [
    this.comparisonToolHelperService.createVisualizationOverviewPane(
      ComparisonToolPage.GeneExpression,
      `<p>Welcome to Agora's Gene Comparison Tool. This overview demonstrates how to use the tool to explore results about genes related to AD. You can revisit this walkthrough by clicking the Visualization Overview link at the bottom of the page.</p>
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

  // TODO MG-485 - Update overview panes content and images
  viewConfig: Partial<ComparisonToolViewConfig> = {
    selectorsWikiParams: this.selectorsWikiParams,
    headerTitle: ComparisonToolPage.GeneExpression,
    filterResultsButtonTooltip: 'Filter results by Model, Biological Domain, and more',
    viewDetailsTooltip: 'Open gene details page',
    viewDetailsClick: (id: string, label: string) => {
      // TODO add logic to display details pages MG-588
    },
    legendPanelConfig: this.legendPanelConfig,
    visualizationOverviewPanes: this.visualizationOverviewPanes,
    rowsPerPage: 10,
    rowIdDataKey: 'composite_id',
  };

  constructor() {
    this.comparisonToolService.setViewConfig(this.viewConfig);
  }

  private loadData(
    selection: string[],
    pinnedItems: string[],
    pageNumber: number,
    pageSize: number,
    searchTerm: string | null,
  ) {
    this.getPinnedData(selection, pinnedItems);
    this.getUnpinnedData(selection, pinnedItems, pageNumber, pageSize, searchTerm);
  }

  readonly onUpdateEffect = effect(() => {
    if (this.platformService.isBrowser && this.isInitialized()) {
      const selection = this.comparisonToolService.dropdownSelection();
      if (!selection.length) {
        return;
      }

      const pinnedItems = Array.from(this.pinnedItems());
      this.loadData(
        selection,
        pinnedItems,
        this.currentPageNumber(),
        this.currentPageSize(),
        this.searchTerm(),
      );
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

  getUnpinnedData(
    selection: string[],
    pinnedItems: string[],
    pageNumber: number,
    pageSize: number,
    searchTerm: string | null,
  ) {
    const query: GeneExpressionSearchQuery = {
      categories: selection,
      items: pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Exclude,
      pageNumber,
      pageSize,
      search: searchTerm,
    };

    this.geneExpressionService
      .getGeneExpressions(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: GeneExpressionsPage) => {
          const data = response.geneExpressions;
          this.comparisonToolService.setUnpinnedData(data);
          this.comparisonToolService.totalResultsCount.set(response.page.totalElements);
        },
        error: (error) => {
          console.error('Error in getUnpinnedData:', error);
          throw new Error('Error fetching gene expression data:', { cause: error });
        },
      });
  }

  getPinnedData(selection: string[], pinnedItems: string[]) {
    const query: GeneExpressionSearchQuery = {
      categories: selection,
      items: pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Include,
    };

    this.geneExpressionService
      .getGeneExpressions(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: GeneExpressionsPage) => {
          const data = response.geneExpressions;
          this.comparisonToolService.setPinnedData(data);
          this.comparisonToolService.pinnedResultsCount.set(data.length);
        },
        error: (error) => {
          throw new Error('Error fetching gene expression data:', { cause: error });
        },
      });
  }
}
