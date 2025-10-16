import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import {
  ComparisonToolConfig,
  ComparisonToolConfigService,
  ComparisonToolPage,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { GeneExpressionHelpLinksComponent } from './components/gene-expression-help-links/gene-expression-help-links.component';

@Component({
  selector: 'model-ad-gene-expression-comparison-tool',
  imports: [BaseComparisonToolComponent, GeneExpressionHelpLinksComponent],
  templateUrl: './gene-expression-comparison-tool.component.html',
  styleUrls: ['./gene-expression-comparison-tool.component.scss'],
  providers: [ComparisonToolService, ComparisonToolFilterService],
})
export class GeneExpressionComparisonToolComponent implements OnInit {
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  isLoading = signal(true);
  resultsCount = signal(50000);

  configs: ComparisonToolConfig[] = [];
  selectorsWikiParams: { [key: string]: SynapseWikiParams } = {
    'RNA - DIFFERENTIAL EXPRESSION': {
      ownerId: 'syn66271427',
      wikiId: '632873',
    },
  };

  ngOnInit() {
    // TODO - Replace with actual data fetching logic
    setTimeout(() => {
      this.isLoading.set(false);
    }, 300);

    this.comparisonToolConfigService
      .getComparisonToolConfig(ComparisonToolPage.GeneExpression)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (configs: ComparisonToolConfig[]) => {
          this.configs = configs;
        },
        error: (error) => {
          console.error('Error retrieving comparison tool config: ', error);
          this.router.navigateByUrl(ROUTE_PATHS.ERROR, { skipLocationChange: true });
        },
      });
  }
}
