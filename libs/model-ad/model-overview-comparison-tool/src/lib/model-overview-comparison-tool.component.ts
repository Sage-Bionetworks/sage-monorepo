import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import {
  BaseComparisonToolComponent,
  ComparisonToolColumnsComponent,
  DisplayedResultsComponent,
} from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import { ComparisonToolService, PlatformService } from '@sagebionetworks/explorers/services';
import {
  ComparisonToolConfig,
  ModelOverview,
  ModelOverviewService,
} from '@sagebionetworks/model-ad/api-client';
import { ModelOverviewHelpLinksComponent } from './components/model-overview-help-links/model-overview-help-links.component';
import { ModelOverviewMainTableComponent } from './components/model-overview-main-table/model-overview-main-table.component';

@Component({
  selector: 'model-ad-model-overview-comparison-tool',
  imports: [
    BaseComparisonToolComponent,
    ModelOverviewMainTableComponent,
    ModelOverviewHelpLinksComponent,
    DisplayedResultsComponent,
    ComparisonToolColumnsComponent,
  ],
  providers: [ComparisonToolService],
  templateUrl: './model-overview-comparison-tool.component.html',
  styleUrls: ['./model-overview-comparison-tool.component.scss'],
})
export class ModelOverviewComparisonToolComponent implements OnInit {
  private platformService = inject(PlatformService);
  private destroyRef = inject(DestroyRef);

  private readonly comparisonToolService = inject(ComparisonToolService);
  private readonly modelOverviewService = inject(ModelOverviewService);

  private readonly route = inject(ActivatedRoute);

  data: ModelOverview[] = [];

  isLoading = signal(true);
  columns = this.comparisonToolService.columns;
  resultsCount = this.comparisonToolService.totalResultsCount;

  config!: ComparisonToolConfig;

  constructor() {
    if (this.platformService.isBrowser) {
      this.config = this.getConfig();
      this.columns.set(this.getColumnsFromConfig(this.config));
    }
  }

  ngOnInit() {
    if (this.platformService.isBrowser) {
      this.getData();
    }
  }

  getConfig() {
    return this.route.snapshot.data['comparisonToolConfig'];
  }

  getColumnsFromConfig(config: ComparisonToolConfig) {
    let columns: string[] = [];
    if (config && config.columns) {
      columns = config.columns.map((column) => column.name);
      return columns;
    } else {
      throw new Error(
        'Missing comparison tool config data: "columns" property is undefined or empty.',
      );
    }
  }

  getData() {
    this.modelOverviewService
      .getModelOverviews()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.data = data;
        },
        error: (error) => {
          throw new Error('Error fetching model overview data:', { cause: error });
        },
        complete: () => {
          this.isLoading.set(false);
        },
      });
  }

  setFilters = (filters: ComparisonToolFilter[]) => {
    // TODO: filter data based on selected filters
    console.log('Filters changed:', filters);
  };
}
