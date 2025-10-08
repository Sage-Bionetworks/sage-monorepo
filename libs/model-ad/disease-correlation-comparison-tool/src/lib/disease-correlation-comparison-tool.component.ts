import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import {
  ComparisonToolConfig,
  ComparisonToolConfigService,
  ComparisonToolPage,
} from '@sagebionetworks/model-ad/api-client';
import { isEqual } from 'lodash';
import { DiseaseCorrelationHelpLinksComponent } from './components/disease-correlation-help-links/disease-correlation-help-links.component';

@Component({
  selector: 'model-ad-disease-correlation-comparison-tool',
  imports: [BaseComparisonToolComponent, DiseaseCorrelationHelpLinksComponent],
  templateUrl: './disease-correlation-comparison-tool.component.html',
  styleUrls: ['./disease-correlation-comparison-tool.component.scss'],
  providers: [ComparisonToolService],
})
export class DiseaseCorrelationComparisonToolComponent implements OnInit {
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  isLoading = signal(true);
  resultsCount = signal(40000);

  configs: ComparisonToolConfig[] = [];
  config: ComparisonToolConfig | undefined;

  // TODO: update to reflect current selectors (MG-425)
  dropdowns = ['CONSENSUS NETWORK MODULES', 'Consensus Cluster A - ECM Organization'];

  ngOnInit() {
    // TODO - Replace with actual data fetching logic
    setTimeout(() => {
      this.isLoading.set(false);
    }, 300);

    this.comparisonToolConfigService
      .getComparisonToolConfig(ComparisonToolPage.DiseaseCorrelation)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (configs: ComparisonToolConfig[]) => {
          this.configs = configs;
          this.config = this.setCurrentConfig();
        },
        error: (error) => {
          console.error('Error retrieving comparison tool config: ', error);
          this.router.navigateByUrl('/not-found', { skipLocationChange: true });
        },
      });
  }

  setCurrentConfig() {
    return this.configs.find((config) => {
      return isEqual(config.dropdowns, this.dropdowns);
    });
  }

  setFilters = (filters: ComparisonToolFilter[]) => {
    // TODO: filter data based on selected filters
    console.log('Filters changed:', filters);
  };
}
