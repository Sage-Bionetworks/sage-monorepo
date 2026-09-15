import { inject, Provider } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ComparisonToolConfig,
  ComparisonToolUrlParams,
  ComparisonToolViewConfig,
} from '@sagebionetworks/explorers/models';
import { SortMeta } from 'primeng/api';
import { BehaviorSubject, of } from 'rxjs';
import { ComparisonToolUrlService } from './comparison-tool-url.service';
import { ComparisonToolService, PinAllFetch } from './comparison-tool.service';
import { ToastNotificationService } from './toast-notification.service';

const noMatchingRows: PinAllFetch<Record<string, unknown>> = () =>
  of({ rows: [], totalElements: 0 });

export type ComparisonToolServiceOptions = {
  configs?: ComparisonToolConfig[];
  selection?: string[];
  totalResultsCount?: number;
  legendVisible?: boolean;
  visualizationOverviewVisibility?: boolean;
  viewConfig?: Partial<ComparisonToolViewConfig>;
  maxPinnedItems?: number;
  pinAllFetch?: PinAllFetch<Record<string, unknown>>;
  pinnedItems?: string[];
  unpinnedData?: Record<string, unknown>[];
  pinnedData?: Record<string, unknown>[];
  multiSortMeta?: SortMeta[];
  router?: Router;
  activatedRoute?: ActivatedRoute;
  urlSync?: boolean;
};

class ComparisonToolUrlServiceStub {
  params$ = new BehaviorSubject<ComparisonToolUrlParams>({});
  syncToUrl(): void {
    return;
  }
  clearUrl(): void {
    return;
  }
}

export const provideComparisonToolService = (
  options?: ComparisonToolServiceOptions,
): Provider[] => {
  const useUrlSync = options?.urlSync ?? false;

  const providers: Provider[] = [ToastNotificationService];

  if (useUrlSync) {
    providers.push(ComparisonToolUrlService);
  } else {
    providers.push({ provide: ComparisonToolUrlService, useClass: ComparisonToolUrlServiceStub });
  }

  if (options?.router) {
    providers.push({ provide: Router, useValue: options.router });
  }
  if (options?.activatedRoute) {
    providers.push({ provide: ActivatedRoute, useValue: options.activatedRoute });
  }

  providers.push({
    provide: ComparisonToolService,
    useFactory: () => {
      const service = new ComparisonToolService<Record<string, unknown>>();
      const urlService = inject(ComparisonToolUrlService);

      if (!options) return service;

      if (options.configs) {
        service.connect({
          config$: of(options.configs),
          queryParams$: urlService.params$,
          pinAllFetch: options.pinAllFetch ?? noMatchingRows,
          initialSelection: options.selection,
        });
      } else if (options.selection) {
        service.setDropdownSelection(options.selection);
      }

      if (options.viewConfig) {
        service.setViewConfig(options.viewConfig);
      }

      if (options.totalResultsCount !== undefined) {
        service.totalResultsCount.set(options.totalResultsCount);
      }

      if (options.legendVisible !== undefined) {
        service.setLegendVisibility(options.legendVisible);
      }

      if (options.visualizationOverviewVisibility !== undefined) {
        service.setVisualizationOverviewVisibility(options.visualizationOverviewVisibility);
      }

      if (options.maxPinnedItems !== undefined) {
        service.setMaxPinnedItems(options.maxPinnedItems);
      }

      if (options.pinnedItems !== undefined) {
        service.setPinnedItems(options.pinnedItems);
      }

      if (options.unpinnedData !== undefined) {
        service.setUnpinnedData(options.unpinnedData);
      }

      if (options.pinnedData !== undefined) {
        service.setPinnedData(options.pinnedData);
      }

      if (options.pinnedData === undefined && options.pinnedItems !== undefined) {
        // If only pinned IDs are provided, initialize pinned data with provided ids
        const pinnedData = options.pinnedItems.map((item) => ({ _id: item }));
        service.setPinnedData(pinnedData);
      }

      if (options.multiSortMeta !== undefined) {
        service.setSort(options.multiSortMeta);
      }

      return service;
    },
  });

  return providers;
};
