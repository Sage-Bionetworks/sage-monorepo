import { inject, Provider } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ComparisonToolConfig,
  ComparisonToolUrlParams,
  ComparisonToolViewConfig,
} from '@sagebionetworks/explorers/models';
import { MessageService } from 'primeng/api';
import { BehaviorSubject, of } from 'rxjs';
import { ComparisonToolUrlService } from './comparison-tool-url.service';
import { ComparisonToolService } from './comparison-tool.service';
import { NotificationService } from './notification.service';

export type ComparisonToolServiceOptions = {
  configs?: ComparisonToolConfig[];
  selection?: string[];
  totalResultsCount?: number;
  legendVisible?: boolean;
  visualizationOverviewVisibility?: boolean;
  visualizationOverviewHiddenByUser?: boolean;
  viewConfig?: Partial<ComparisonToolViewConfig>;
  maxPinnedItems?: number;
  pinnedItems?: string[];
  unpinnedData?: Record<string, unknown>[];
  pinnedData?: Record<string, unknown>[];
  router?: Router;
  activatedRoute?: ActivatedRoute;
  urlSync?: boolean;
};

class ComparisonToolUrlServiceStub {
  params$ = new BehaviorSubject<ComparisonToolUrlParams>({});
  syncToUrl(): void {
    return;
  }
  clearPinnedParam(): void {
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

  if (!options) {
    return [ComparisonToolUrlService, ComparisonToolService];
  }

  const providers: Provider[] = [MessageService, NotificationService];

  if (useUrlSync) {
    providers.push(ComparisonToolUrlService);
  } else {
    providers.push({ provide: ComparisonToolUrlService, useClass: ComparisonToolUrlServiceStub });
  }

  if (options.router) {
    providers.push({ provide: Router, useValue: options.router });
  }
  if (options.activatedRoute) {
    providers.push({ provide: ActivatedRoute, useValue: options.activatedRoute });
  }

  providers.push({
    provide: ComparisonToolService,
    useFactory: () => {
      const service = new ComparisonToolService();
      const urlService = inject(ComparisonToolUrlService);

      if (options.configs) {
        service.connect({
          config$: of(options.configs),
          queryParams$: urlService.params$,
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

      if (options.visualizationOverviewHiddenByUser !== undefined) {
        service.setVisualizationOverviewHiddenByUser(options.visualizationOverviewHiddenByUser);
      }

      if (options.maxPinnedItems !== undefined) {
        service.setMaxPinnedItems(options.maxPinnedItems);
      }

      if (options.pinnedItems !== undefined) {
        service.setPinnedItems(options.pinnedItems);
        service.pinnedResultsCount.set(options.pinnedItems.length);
      }

      if (options.unpinnedData !== undefined) {
        service.setUnpinnedData(options.unpinnedData);
      }

      if (options.pinnedData !== undefined) {
        service.setPinnedData(options.pinnedData);
      }

      return service;
    },
  });

  return providers;
};
