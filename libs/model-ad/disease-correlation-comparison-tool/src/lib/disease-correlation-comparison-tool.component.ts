import { Component, DestroyRef, OnInit, effect, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolViewConfig, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { PlatformService } from '@sagebionetworks/explorers/services';
import {
  ComparisonToolConfig,
  ComparisonToolConfigService,
  ComparisonToolPage,
  DiseaseCorrelationService,
  ItemFilterTypeQuery,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { shareReplay } from 'rxjs';
import { DiseaseCorrelationHelpLinksComponent } from './components/disease-correlation-help-links/disease-correlation-help-links.component';
import { DiseaseCorrelationComparisonToolService } from './services/disease-correlation-comparison-tool.service';

@Component({
  selector: 'model-ad-disease-correlation-comparison-tool',
  imports: [BaseComparisonToolComponent, DiseaseCorrelationHelpLinksComponent],
  templateUrl: './disease-correlation-comparison-tool.component.html',
  styleUrls: ['./disease-correlation-comparison-tool.component.scss'],
})
export class DiseaseCorrelationComparisonToolComponent implements OnInit {
  private readonly platformService = inject(PlatformService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly diseaseCorrelationService = inject(DiseaseCorrelationService);
  private readonly comparisonToolService = inject(DiseaseCorrelationComparisonToolService);

  pinnedItems = this.comparisonToolService.pinnedItems;

  isLoading = signal(true);

  selectorsWikiParams: { [key: string]: SynapseWikiParams } = {
    'CONSENSUS NETWORK MODULES': {
      ownerId: 'syn66271427',
      wikiId: '632874',
    },
  };
  viewConfig: Partial<ComparisonToolViewConfig> = {
    selectorsWikiParams: this.selectorsWikiParams,
    headerTitle: 'Disease Correlation',
    filterResultsButtonTooltip: 'Filter results by Age, Sex, Modified Gene, and more',
    viewDetailsTooltip: 'Open model details page',
    viewDetailsClick: (id: string, label: string) => {
      const url = this.router.serializeUrl(this.router.createUrlTree([ROUTE_PATHS.MODELS, label]));
      window.open(url, '_blank');
    },
  };

  constructor() {
    this.comparisonToolService.setViewConfig(this.viewConfig);

    effect(() => {
      const selection = this.comparisonToolService.dropdownSelection();
      if (!selection.length) {
        return;
      }

      this.isLoading.set(true);
      this.getUnpinnedData();
      this.getPinnedData();
    });
  }

  ngOnInit() {
    if (this.platformService.isBrowser) {
      this.getConfigs();
    }
  }

  getConfigs() {
    // Skip if already initialized (service persists at route level)
    if (this.comparisonToolService.configs().length > 0) {
      return;
    }

    this.comparisonToolConfigService
      .getComparisonToolConfig(ComparisonToolPage.DiseaseCorrelation)
      .pipe(shareReplay(1), takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (configs: ComparisonToolConfig[]) => {
          this.comparisonToolService.initialize(configs);
        },
        error: (error) => {
          console.error('Error retrieving comparison tool config: ', error);
          this.router.navigateByUrl(ROUTE_PATHS.ERROR, { skipLocationChange: true });
        },
      });
  }

  getUnpinnedData() {
    this.diseaseCorrelationService
      .getDiseaseCorrelations(
        this.comparisonToolService.dropdownSelection(),
        [],
        ItemFilterTypeQuery.Exclude,
      )
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

  getPinnedData() {
    this.diseaseCorrelationService
      .getDiseaseCorrelations(
        this.comparisonToolService.dropdownSelection(),
        Array.from(this.pinnedItems()),
        ItemFilterTypeQuery.Include,
      )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.comparisonToolService.setPinnedData(data);
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
