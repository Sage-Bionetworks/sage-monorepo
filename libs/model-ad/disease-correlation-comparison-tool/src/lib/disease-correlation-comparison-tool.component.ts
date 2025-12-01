import { Component, DestroyRef, OnInit, computed, effect, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  ComparisonToolViewConfig,
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
  DiseaseCorrelationService,
  ItemFilterTypeQuery,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { catchError, of, shareReplay } from 'rxjs';
import { DiseaseCorrelationComparisonToolService } from './services/disease-correlation-comparison-tool.service';

@Component({
  selector: 'model-ad-disease-correlation-comparison-tool',
  imports: [ComparisonToolComponent],
  templateUrl: './disease-correlation-comparison-tool.component.html',
  styleUrls: ['./disease-correlation-comparison-tool.component.scss'],
})
export class DiseaseCorrelationComparisonToolComponent implements OnInit {
  private readonly platformService = inject(PlatformService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly comparisonToolHelperService = inject(ComparisonToolHelperService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly diseaseCorrelationService = inject(DiseaseCorrelationService);
  private readonly comparisonToolService = inject(DiseaseCorrelationComparisonToolService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);

  pinnedItems = this.comparisonToolService.pinnedItems;
  isInitialized = this.comparisonToolService.isInitialized;
  readonly isReady = computed(() => this.platformService.isBrowser && this.isInitialized());

  isLoading = signal(true);

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
    colorChartText: `Circle color indicates the correlation between changes in gene expression in the model versus in humans with AD. Red shades indicate a negative correlation, while blue shades indicate a positive correlation.`,
    sizeChartLowerLabel: 'Significant',
    sizeChartUpperLabel: 'Insignificant',
    sizeChartText: `Circle diameter indicates P-value. Larger circles indicate higher statistical significance, while smaller circles indicate lower statistical significance.`,
  };

  // TODO MG-485 - Update overview panes content and images
  viewConfig: Partial<ComparisonToolViewConfig> = {
    selectorsWikiParams: this.selectorsWikiParams,
    headerTitle: ComparisonToolPage.DiseaseCorrelation,
    filterResultsButtonTooltip: 'Filter results by Age, Sex, Modified Gene, and more',
    viewDetailsTooltip: 'Open model details page',
    viewDetailsClick: (id: string, label: string) => {
      const url = this.router.serializeUrl(this.router.createUrlTree([ROUTE_PATHS.MODELS, label]));
      window.open(url, '_blank');
    },
    legendPanelConfig: this.legendPanelConfig,
    visualizationOverviewPanes: [
      this.comparisonToolHelperService.createVisualizationOverviewPane(
        ComparisonToolPage.DiseaseCorrelation,
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
    ],
  };

  constructor() {
    this.comparisonToolService.setViewConfig(this.viewConfig);
  }

  readonly onUpdateEffect = effect(() => {
    if (!this.isReady()) {
      return;
    }

    const selection = this.comparisonToolService.dropdownSelection();
    if (!selection.length) {
      return;
    }

    const pinnedItems = Array.from(this.pinnedItems());
    this.isLoading.set(true);
    this.getUnpinnedData(selection, pinnedItems);
    this.getPinnedData(selection, pinnedItems);
  });

  ngOnInit() {
    if (!this.platformService.isBrowser) {
      return;
    }

    this.comparisonToolService.connect({
      config$: this.config$,
      queryParams$: this.comparisonToolUrlService.params$,
      cacheKey: ComparisonToolPage.DiseaseCorrelation,
    });
  }

  getUnpinnedData(selection: string[], pinnedItems: string[]) {
    this.diseaseCorrelationService
      .getDiseaseCorrelations(selection, pinnedItems, ItemFilterTypeQuery.Exclude)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.comparisonToolService.setUnpinnedData(data);
          this.comparisonToolService.totalResultsCount.set(data.length);
        },
        error: (error) => {
          throw new Error('Error fetching disease correlation data:', { cause: error });
        },
        complete: () => {
          this.isLoading.set(false);
        },
      });
  }

  getPinnedData(selection: string[], pinnedItems: string[]) {
    this.diseaseCorrelationService
      .getDiseaseCorrelations(selection, pinnedItems, ItemFilterTypeQuery.Include)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.comparisonToolService.setPinnedData(data);
          this.comparisonToolService.pinnedResultsCount.set(data.length);
        },
        error: (error) => {
          throw new Error('Error fetching disease correlation data:', { cause: error });
        },
        complete: () => {
          this.isLoading.set(false);
        },
      });
  }
}
