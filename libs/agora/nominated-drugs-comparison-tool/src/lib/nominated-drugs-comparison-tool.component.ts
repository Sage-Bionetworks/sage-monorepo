import { Component, DestroyRef, effect, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import {
  ComparisonToolConfigPage,
  ComparisonToolConfigService,
  ItemFilterTypeQuery,
  NominatedDrug,
  NominatedDrugSearchQuery,
  NominatedDrugService,
  NominatedDrugsPage,
} from '@sagebionetworks/agora/api-client';
import { DEFAULT_SYNAPSE_WIKI_OWNER_ID, ROUTE_PATHS } from '@sagebionetworks/agora/config';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  AppError,
  ComparisonToolQuery,
  ComparisonToolViewConfig,
} from '@sagebionetworks/explorers/models';
import {
  ComparisonToolUrlService,
  LoggerService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import { SortMeta } from 'primeng/api';
import { catchError, EMPTY, shareReplay } from 'rxjs';
import { NominatedDrugsComparisonToolService } from './services/nominated-drugs-comparison-tool.service';

@Component({
  selector: 'agora-nominated-drugs-comparison-tool',
  imports: [ComparisonToolComponent],
  templateUrl: './nominated-drugs-comparison-tool.component.html',
  styleUrls: ['./nominated-drugs-comparison-tool.component.scss'],
})
export class NominatedDrugsComparisonToolComponent implements OnInit, OnDestroy {
  private readonly platformService = inject(PlatformService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly logger = inject(LoggerService);
  private readonly nominatedDrugsService = inject(NominatedDrugService);
  private readonly comparisonToolService = inject(NominatedDrugsComparisonToolService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);

  isInitialized = this.comparisonToolService.isInitialized;
  query = this.comparisonToolService.query;

  readonly config$ = this.comparisonToolConfigService
    .getComparisonToolsConfig(ComparisonToolConfigPage.NominatedDrugs)
    .pipe(
      catchError((error) => {
        this.logger.error('Error retrieving comparison tool config', error);
        return EMPTY;
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

  viewConfig: Partial<ComparisonToolViewConfig> = {
    headerTitle: ComparisonToolConfigPage.NominatedDrugs,
    headerTitleWikiParams: { ownerId: DEFAULT_SYNAPSE_WIKI_OWNER_ID, wikiId: '639223' },
    filterResultsButtonTooltip: 'Filter results by Nominating PI and more',
    showSignificanceControls: false,
    viewDetailsTooltip: 'Open nominated drugs details page',
    viewDetailsClick: (rowData: unknown) => {
      const drug = rowData as NominatedDrug;
      // TODO (AG-1996): replace common_name with chembl_id once available in the backend
      const url = this.router.serializeUrl(
        this.router.createUrlTree([ROUTE_PATHS.DRUG_DETAILS, drug.common_name]),
      );
      window.open(url, '_blank');
    },
    legendEnabled: false,
    rowsPerPage: 10,
    rowIdDataKey: 'common_name',
    allowPinnedImageDownload: false,
    defaultSort: [
      { field: 'total_nominations', order: -1 },
      { field: 'common_name', order: 1 },
    ],
  };

  constructor() {
    this.comparisonToolService.setViewConfig(this.viewConfig);
  }

  readonly pinnedDataEffect = effect(() => {
    if (this.platformService.isBrowser && this.isInitialized()) {
      const pinnedItems = this.comparisonToolService.pinnedItems();
      const sortMeta = this.comparisonToolService.multiSortMeta();
      this.getPinnedData(pinnedItems, sortMeta);
    }
  });

  readonly unpinnedDataEffect = effect(() => {
    if (this.platformService.isBrowser && this.isInitialized()) {
      const query = this.query();
      this.getUnpinnedData(query);
    }
  });

  ngOnInit() {
    if (this.platformService.isServer) {
      return;
    }

    this.comparisonToolService.connect({
      config$: this.config$,
      queryParams$: this.comparisonToolUrlService.params$,
    });
  }

  ngOnDestroy() {
    this.comparisonToolService.disconnect();
  }

  getUnpinnedData(currentQuery: ComparisonToolQuery) {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(
      currentQuery.multiSortMeta,
    );

    const selectedFilters = this.comparisonToolService.selectedFilters();

    const query: NominatedDrugSearchQuery = {
      items: currentQuery.pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Exclude,
      pageNumber: currentQuery.pageNumber,
      pageSize: currentQuery.pageSize,
      search: currentQuery.searchTerm,
      principalInvestigators: selectedFilters['nominatingPis'],
      totalNominations: selectedFilters['totalNominations']?.map(Number) ?? [],
      sortFields,
      sortOrders,
    };

    this.comparisonToolService.startFetch();
    this.nominatedDrugsService
      .getNominatedDrugs(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: NominatedDrugsPage) => {
          const data = response.nominatedDrugs;
          this.comparisonToolService.setUnpinnedData(data);
          this.comparisonToolService.totalResultsCount.set(response.page.totalElements);
        },
        error: () => {
          throw new AppError(
            'Unable to load unpinned nominated drugs data. Please reload the page.',
            true,
          );
        },
      });
  }

  getPinnedData(pinnedItems: string[], sortMeta: SortMeta[]) {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(sortMeta);

    const query: NominatedDrugSearchQuery = {
      items: pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Include,
      sortFields,
      sortOrders,
    };

    this.comparisonToolService.startFetch();
    this.nominatedDrugsService
      .getNominatedDrugs(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: NominatedDrugsPage) => {
          const data = response.nominatedDrugs;
          this.comparisonToolService.setPinnedData(data);
          this.comparisonToolService.pinnedResultsCount.set(data.length);
        },
        error: () => {
          throw new AppError(
            'Unable to load pinned nominated drugs data. Please reload the page.',
            true,
          );
        },
      });
  }
}
