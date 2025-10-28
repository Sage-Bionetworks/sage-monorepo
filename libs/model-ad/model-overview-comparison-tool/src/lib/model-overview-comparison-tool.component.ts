import { Component, DestroyRef, effect, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolViewConfig } from '@sagebionetworks/explorers/models';
import { PlatformService } from '@sagebionetworks/explorers/services';
import {
  ComparisonToolConfig,
  ComparisonToolConfigService,
  ComparisonToolPage,
  ItemFilterTypeQuery,
  ModelOverviewService,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { shareReplay } from 'rxjs';
import { ModelOverviewHelpLinksComponent } from './components/model-overview-help-links/model-overview-help-links.component';
import { ModelOverviewComparisonToolService } from './services/model-overview-comparison-tool.service';

@Component({
  selector: 'model-ad-model-overview-comparison-tool',
  imports: [BaseComparisonToolComponent, ModelOverviewHelpLinksComponent],
  templateUrl: './model-overview-comparison-tool.component.html',
  styleUrls: ['./model-overview-comparison-tool.component.scss'],
})
export class ModelOverviewComparisonToolComponent implements OnInit {
  private readonly platformService = inject(PlatformService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly comparisonToolService = inject(ModelOverviewComparisonToolService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly modelOverviewService = inject(ModelOverviewService);

  pinnedItems = this.comparisonToolService.pinnedItems;

  isLoading = signal(true);

  viewConfig: Partial<ComparisonToolViewConfig> = {
    headerTitle: 'Model Overview',
    filterResultsButtonTooltip: 'Filter results by Model Type, Modified Gene, and more',
    showSignificanceControls: false,
    viewDetailsTooltip: 'Open model details page',
    viewDetailsClick: (id: string, label: string) => {
      const url = this.router.serializeUrl(this.router.createUrlTree([ROUTE_PATHS.MODELS, label]));
      window.open(url, '_blank');
    },
  };

  constructor() {
    this.comparisonToolService.setViewConfig(this.viewConfig);
  }

  readonly onUpdateEffect = effect(() => {
    if (this.platformService.isBrowser) {
      this.isLoading.set(true);
      const pinnedItems = Array.from(this.pinnedItems());
      this.getPinnedData(pinnedItems);
      this.getUnpinnedData(pinnedItems);
    }
  });

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
      .getComparisonToolConfig(ComparisonToolPage.ModelOverview)
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

  getUnpinnedData(pinnedItems: string[]) {
    this.modelOverviewService
      .getModelOverviews(pinnedItems, ItemFilterTypeQuery.Exclude)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.comparisonToolService.setUnpinnedData(data);
          this.comparisonToolService.totalResultsCount.set(data.length);
        },
        error: (error) => {
          throw new Error('Error fetching model overview data:', { cause: error });
        },
        complete: () => {
          this.isLoading.set(false);
        },
      });
  }

  getPinnedData(pinnedItems: string[]) {
    this.modelOverviewService
      .getModelOverviews(pinnedItems, ItemFilterTypeQuery.Include)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.comparisonToolService.setPinnedData(data);
        },
        error: (error) => {
          throw new Error('Error fetching model overview data:', { cause: error });
        },
        complete: () => {
          this.isLoading.set(false);
        },
      });
  }
}
