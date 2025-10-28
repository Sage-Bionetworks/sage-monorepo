import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolViewConfig, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { ComparisonToolService, PlatformService } from '@sagebionetworks/explorers/services';
import {
  ComparisonToolConfig,
  ComparisonToolConfigService,
  ComparisonToolPage,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { shareReplay } from 'rxjs';
import { GeneExpressionHelpLinksComponent } from './components/gene-expression-help-links/gene-expression-help-links.component';

@Component({
  selector: 'model-ad-gene-expression-comparison-tool',
  imports: [BaseComparisonToolComponent, GeneExpressionHelpLinksComponent],
  templateUrl: './gene-expression-comparison-tool.component.html',
  styleUrls: ['./gene-expression-comparison-tool.component.scss'],
})
export class GeneExpressionComparisonToolComponent implements OnInit {
  private readonly platformService = inject(PlatformService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly comparisonToolService = inject(ComparisonToolService);
  // private readonly geneExpressionService = inject(GeneExpressionService)

  isLoading = signal(true);
  selectorsWikiParams: { [key: string]: SynapseWikiParams } = {
    'RNA - DIFFERENTIAL EXPRESSION': {
      ownerId: 'syn66271427',
      wikiId: '632873',
    },
  };
  viewConfig: ComparisonToolViewConfig = {
    selectorsWikiParams: this.selectorsWikiParams,
  };

  ngOnInit() {
    if (this.platformService.isBrowser) {
      this.getConfigs();
      this.getData();
    }
  }

  getConfigs() {
    // Skip if already initialized (service persists at route level)
    if (this.comparisonToolService.configs().length > 0) {
      return;
    }

    this.comparisonToolConfigService
      .getComparisonToolConfig(ComparisonToolPage.GeneExpression)
      .pipe(shareReplay(1), takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (configs: ComparisonToolConfig[]) => {
          this.comparisonToolService.initialize(configs, undefined, this.viewConfig);
          this.comparisonToolService.totalResultsCount.set(50000);
        },
        error: (error) => {
          console.error('Error retrieving comparison tool config: ', error);
          this.router.navigateByUrl(ROUTE_PATHS.ERROR, { skipLocationChange: true });
        },
      });
  }

  getData() {
    // this.geneExpressionService
    //   .getModelOverviews()
    //   .pipe(takeUntilDestroyed(this.destroyRef))
    //   .subscribe({
    //     next: (data) => {
    //       this.data = data;
    //       this.comparisonToolService.totalResultsCount.set(data.length);
    //     },
    //     error: (error) => {
    //       throw new Error('Error fetching model overview data:', { cause: error });
    //     },
    //     complete: () => {
    //       this.isLoading.set(false);
    //     },
    //   });
    this.isLoading.set(false);
  }
}
