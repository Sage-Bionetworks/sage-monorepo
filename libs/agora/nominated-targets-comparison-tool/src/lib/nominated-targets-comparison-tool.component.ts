import { Component, DestroyRef, effect, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import {
  ComparisonToolConfigPage,
  ComparisonToolConfigService,
  ItemFilterTypeQuery,
  NominatedTarget,
  NominatedTargetSearchQuery,
  NominatedTargetService,
  NominatedTargetsPage,
} from '@sagebionetworks/agora/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/agora/config';
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
import { NominatedTargetsComparisonToolService } from './services/nominated-targets-comparison-tool.service';

@Component({
  selector: 'agora-nominated-targets-comparison-tool',
  imports: [ComparisonToolComponent],
  templateUrl: './nominated-targets-comparison-tool.component.html',
  styleUrls: ['./nominated-targets-comparison-tool.component.scss'],
})
export class NominatedTargetsComparisonToolComponent implements OnInit, OnDestroy {
  private readonly platformService = inject(PlatformService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly logger = inject(LoggerService);
  private readonly nominatedTargetsService = inject(NominatedTargetService);
  private readonly comparisonToolService = inject(NominatedTargetsComparisonToolService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);

  isInitialized = this.comparisonToolService.isInitialized;
  query = this.comparisonToolService.query;

  readonly config$ = this.comparisonToolConfigService
    .getComparisonToolsConfig(ComparisonToolConfigPage.NominatedTargets)
    .pipe(
      catchError((error) => {
        this.logger.error('Error retrieving comparison tool config', error);
        return EMPTY;
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

  viewConfig: Partial<ComparisonToolViewConfig> = {
    headerTitle: ComparisonToolConfigPage.NominatedTargets,
    headerTitleWikiParams: { ownerId: 'syn25913473', wikiId: '639222' },
    filterResultsButtonTooltip:
      'Filter results by Nominating Team, Program, Pharos Class, and more',
    showSignificanceControls: false,
    viewDetailsTooltip: 'Open gene details',
    viewDetailsClick: (rowData: unknown) => {
      const data = rowData as NominatedTarget;
      const url = this.router.serializeUrl(
        this.router.createUrlTree([ROUTE_PATHS.DETAILS, data.ensembl_gene_id]),
      );
      window.open(url, '_blank');
    },
    legendEnabled: false,
    rowsPerPage: 10,
    rowIdDataKey: 'hgnc_symbol',
    allowPinnedImageDownload: false,
    defaultSort: [
      { field: 'total_nominations', order: -1 },
      { field: 'hgnc_symbol', order: 1 },
    ],
  };

  constructor() {
    this.comparisonToolService.setViewConfig(this.viewConfig);
  }

  // Effect for pinned data - only re-fetch when pinnedItems or sort change
  readonly pinnedDataEffect = effect(() => {
    if (this.platformService.isBrowser && this.isInitialized()) {
      const pinnedItems = this.comparisonToolService.pinnedItems();
      const sortMeta = this.comparisonToolService.multiSortMeta();
      this.getPinnedData(pinnedItems, sortMeta);
    }
  });

  // Effect for unpinned data - re-fetch when any query parameter changes
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

    const query: NominatedTargetSearchQuery = {
      items: currentQuery.pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Exclude,
      pageNumber: currentQuery.pageNumber,
      pageSize: currentQuery.pageSize,
      search: currentQuery.searchTerm,
      cohortStudies: selectedFilters['cohorts'],
      inputData: selectedFilters['data'],
      initialNomination: selectedFilters['firstNominations']?.map(Number) ?? [],
      nominatingTeams: selectedFilters['teams'],
      pharosClass: selectedFilters['pharosClass'],
      programs: selectedFilters['programs'],
      totalNominations: selectedFilters['totalNominations']?.map(Number) ?? [],
      sortFields,
      sortOrders,
    };

    this.comparisonToolService.startFetch();
    this.nominatedTargetsService
      .getNominatedTargets(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: NominatedTargetsPage) => {
          const data = response.nominatedTargets;
          this.comparisonToolService.setUnpinnedData(data);
          this.comparisonToolService.totalResultsCount.set(response.page.totalElements);
        },
        error: () => {
          throw new AppError(
            'Unable to load unpinned nominated targets data. Please reload the page.',
            true,
          );
        },
      });
  }

  getPinnedData(pinnedItems: string[], sortMeta: SortMeta[]) {
    const { sortFields, sortOrders } = this.comparisonToolService.convertSortMetaToArrays(sortMeta);

    const query: NominatedTargetSearchQuery = {
      items: pinnedItems,
      itemFilterType: ItemFilterTypeQuery.Include,
      sortFields,
      sortOrders,
    };

    this.comparisonToolService.startFetch();
    this.nominatedTargetsService
      .getNominatedTargets(query)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: NominatedTargetsPage) => {
          const data = response.nominatedTargets;
          this.comparisonToolService.setPinnedData(data);
          this.comparisonToolService.pinnedResultsCount.set(data.length);
        },
        error: () => {
          throw new AppError(
            'Unable to load pinned nominated targets data. Please reload the page.',
            true,
          );
        },
      });
  }
}
