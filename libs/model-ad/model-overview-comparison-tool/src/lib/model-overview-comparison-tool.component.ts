import { Component, inject, signal, OnInit, DestroyRef, computed } from '@angular/core';
import {
  BaseComparisonToolComponent,
  ComparisonToolColumnsComponent,
  ComparisonToolControlsComponent,
  ComparisonToolHeaderComponent,
  FilterPanelComponent,
  FilterPanelService,
} from '@sagebionetworks/explorers/comparison-tools';
import { ModelOverviewHelpLinksComponent } from './components/model-overview-help-links/model-overview-help-links.component';
import {
  ComparisonToolConfig,
  ComparisonToolPage,
  ModelOverview,
  ModelOverviewService,
} from '@sagebionetworks/model-ad/api-client';
import { ActivatedRoute, Router } from '@angular/router';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ModelOverviewMainTableComponent } from './components/model-overview-main-table/model-overview-main-table.component';
import { ComparisonToolConfigService } from '@sagebionetworks/model-ad/api-client';
import { finalize } from 'rxjs/internal/operators/finalize';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';

@Component({
  selector: 'model-ad-model-overview-comparison-tool',
  imports: [
    BaseComparisonToolComponent,
    ComparisonToolHeaderComponent,
    ModelOverviewMainTableComponent,
    ModelOverviewHelpLinksComponent,
    FilterPanelComponent,
    ComparisonToolControlsComponent,
    ComparisonToolColumnsComponent,
  ],
  providers: [ComparisonToolService],
  templateUrl: './model-overview-comparison-tool.component.html',
  styleUrls: ['./model-overview-comparison-tool.component.scss'],
})
export class ModelOverviewComparisonToolComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  router = inject(Router);

  private readonly comparisonToolService = inject(ComparisonToolService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly modelOverviewService = inject(ModelOverviewService);

  private readonly route = inject(ActivatedRoute);
  private readonly filterPanelService = inject(FilterPanelService);

  data = signal<ModelOverview[]>([]);

  isLoading = signal(true);
  columns = this.comparisonToolService.columns;
  resultsCount = this.comparisonToolService.totalResultsCount;

  config = signal<ComparisonToolConfig | undefined>(undefined);
  filters = computed(() => {
    const configFilters = this.config()?.filters ?? [];
    const result: ComparisonToolFilter[] = configFilters.map((filter) => ({
      name: filter.name,
      field: filter.field,
      options: filter.values.map((option) => ({
        label: option,
        selected: false,
      })),
    }));
    return result;
  });

  sortField = '';
  sortOrder = -1;

  searchTerm = '';

  urlParams: { [key: string]: any } | undefined;
  // urlParamsSubscription: Subscription | undefined;

  pinnedItems: ModelOverview[] = [];

  private DEFAULT_SIGNIFICANCE_THRESHOLD = 0.05;
  significanceThreshold = this.DEFAULT_SIGNIFICANCE_THRESHOLD;
  significanceThresholdActive = false;

  ngOnInit() {
    this.comparisonToolConfigService
      .getComparisonToolConfigs()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => {
          this.isLoading.set(false);
        }),
      )
      .subscribe({
        next: (configs) => {
          const modelOverviewConfig = configs.find(
            (config) => config.page === ComparisonToolPage.ModelOverview,
          );
          console.log('modelOverviewConfig', modelOverviewConfig);
          this.config.set(modelOverviewConfig);
          console.log('this.config', this.config());
          if (modelOverviewConfig) {
            this.columns.set(this.getColumnsFromConfig(modelOverviewConfig));
            this.filterPanelService.setFilters(this.filters());
            this.getData();
          } else {
            console.error('No comparison tool config found for ModelOverview page.');
            throw new Error('Comparison tool config not found for ModelOverview page.');
          }
        },
        error: (error) => {
          console.error('Error fetching comparison tool configs:', error);
          throw new Error('Error fetching comparison tool configs:', { cause: error });
        },
      });
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
        next: (overviews) => {
          this.data.set(overviews);
        },
        error: (error) => {
          throw new Error('Error fetching model overview data:', { cause: error });
        },
        complete: () => {
          this.isLoading.set(false);
        },
      });
  }

  // setFilters() {
  //   console.log('setFilters called');
  //   this.filter();
  //   this.updateUrl();
  // }

  filter() {
    let filters: { [key: string]: any };
    console.log('in filter() func of model overview comparison tool');

    // if (this.searchTerm) {
    //   if (this.searchTerm.indexOf(',') !== -1) {
    //     const terms = this.searchTerm
    //       .toLowerCase()
    //       .split(',')
    //       .map((t: string) => t.trim())
    //       .filter((t: string) => t !== '');
    //     filters['search_array'] = {
    //       value: terms,
    //       matchMode: 'intersect',
    //     };
    //   } else {
    //     filters['search_string'] = {
    //       value: this.searchTerm.toLowerCase(),
    //       matchMode: 'contains',
    //     };
    //   }
    // }

    // this.filters.forEach((filter) => {
    //   if (!filter.field) {
    //     return;
    //   }

    //   const values = filter.options
    //     .filter((option) => option.selected)
    //     .map((selected) => selected.value);

    //   if (values.length) {
    //     filters[filter.field] = {
    //       value: values,
    //       matchMode: filter.matchMode || 'equals',
    //     };
    //   }
    // });

    // const filterChanged =
    //   JSON.stringify({ ...filters, ...{ ensembl_gene_id: '' } }) !==
    //   JSON.stringify({
    //     ...this.genesTable.filters,
    //     ...{ ensembl_gene_id: '' },
    //   });

    // const currentPage = this.genesTable._first;

    // this.genesTable.filters = filters;
    // this.genesTable._filter();

    // // Restoring current pagination if filters didn't change
    // if (!filterChanged) {
    //   this.genesTable._first = currentPage;
    // }
  }

  updateUrl() {
    const params: { [key: string]: any } = this.filterPanelService.getSelectedFilters();
    if (this.sortField && this.sortField !== this.columns()[0]) {
      params['sortField'] = this.sortField;
    }
    if (this.sortOrder != -1) {
      params['sortOrder'] = this.sortOrder;
    }
    if (this.pinnedItems.length > 0) {
      // params['pinned'] = this.pinnedItems.map((g: ModelOverview) => g.uid);
      params['pinned'] = this.pinnedItems.map((g: ModelOverview) => g.name);
      params['pinned'].sort();
    }
    if (this.significanceThresholdActive) {
      params['significance'] = this.significanceThreshold;
    }
    this.urlParams = params;
    let url = this.router.serializeUrl(this.router.createUrlTree(['/comparison/model']));
    if (Object.keys(params).length > 0) {
      url += '?' + new URLSearchParams(params);
    }
    window.history.pushState(null, '', url);
  }
}
