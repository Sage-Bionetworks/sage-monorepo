import { Component, inject, signal, OnInit, DestroyRef } from '@angular/core';
import {
  BaseComparisonToolComponent,
  ComparisonToolColumnsComponent,
  ComparisonToolHeaderComponent,
  DisplayedResultsComponent,
} from '@sagebionetworks/explorers/comparison-tools';
import { ModelOverviewHelpLinksComponent } from './components/model-overview-help-links/model-overview-help-links.component';
import {
  ComparisonToolConfig,
  ModelOverview,
  ModelOverviewService,
} from '@sagebionetworks/model-ad/api-client-angular';
import { ActivatedRoute } from '@angular/router';
import { ComparisonToolService, PlatformService } from '@sagebionetworks/explorers/services';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ModelOverviewMainTableComponent } from './components/model-overview-main-table/model-overview-main-table.component';
import { runInBrowser } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'model-ad-model-overview-comparison-tool',
  imports: [
    BaseComparisonToolComponent,
    ComparisonToolHeaderComponent,
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
    runInBrowser(() => {
      this.config = this.getConfig();
      this.columns.set(this.getColumnsFromConfig(this.config));
    }, this.platformService.platformId);
  }

  ngOnInit() {
    runInBrowser(() => {
      this.getData();
    }, this.platformService.platformId);
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
}
