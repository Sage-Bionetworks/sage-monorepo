import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import { ComparisonToolUrlService, PlatformService } from '@sagebionetworks/explorers/services';
import { ComparisonToolConfigService, ComparisonToolPage } from '@sagebionetworks/qtl/api-client';
import { of, shareReplay } from 'rxjs';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { EqtlComparisonToolService } from './services/eqtl-comparison-tool.service';

@Component({
  selector: 'qtl-eqtl-comparison-tool',
  imports: [ComparisonToolComponent, SidebarComponent],
  templateUrl: './eqtl-comparison-tool.component.html',
  styleUrls: ['./eqtl-comparison-tool.component.scss'],
})
export class EqtlComparisonToolComponent implements OnInit, OnDestroy {
  private readonly platformService = inject(PlatformService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly comparisonToolService = inject(EqtlComparisonToolService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);

  isInitialized = this.comparisonToolService.isInitialized;

  readonly config$ = this.comparisonToolConfigService
    .getComparisonToolConfig(ComparisonToolPage.EQtls)
    .pipe(shareReplay({ bufferSize: 1, refCount: true }));

  // TODO(QTL-113): uncomment and populate when CT data fetching is implemented
  // viewConfig: Partial<ComparisonToolViewConfig> = {
  //   rowSelectionEnabled: true,
  //   rowHoverEnabled: true,
  //   rowIdDataKey: 'some_id_field',
  //   // ... other config
  // };

  ngOnInit() {
    if (this.platformService.isServer) {
      return;
    }

    this.comparisonToolService.connect({
      page: ComparisonToolPage.EQtls,
      config$: this.config$,
      queryParams$: this.comparisonToolUrlService.params$,
      // TODO(EX-251): fetch the matching rows for Pin All
      pinAllFetch: () => of({ rows: [], totalElements: 0 }),
    });
  }

  ngOnDestroy() {
    this.comparisonToolService.disconnect();
  }

  // TODO(QTL-113): add effects and fetch methods when CT data fetching is implemented.
  // Follow the same pattern as agora CTs (e.g. NominatedTargetsComparisonToolComponent):
  //
  // readonly unpinnedDataEffect = effect(() => {
  //   if (this.platformService.isBrowser && this.comparisonToolService.isInitialized()) {
  //     const query = this.comparisonToolService.query();
  //     this.getUnpinnedData(query);
  //   }
  // });
  //
  // readonly pinnedDataEffect = effect(() => {
  //   if (this.platformService.isBrowser && this.comparisonToolService.isInitialized()) {
  //     const pinnedItems = this.comparisonToolService.pinnedItems();
  //     const sortMeta = this.comparisonToolService.multiSortMeta();
  //     this.getPinnedData(pinnedItems, sortMeta);
  //   }
  // });
  //
  // getUnpinnedData(query: ComparisonToolQuery) {
  //   const selectedRowId = this.comparisonToolService.selectedRowId();
  //   this.comparisonToolService.fetchUnpinned(
  //     this.eqtlService.getEqtlData({ ...query, selectedRowId }).pipe(
  //       map((response) => ({ data: response.data, totalCount: response.page.totalElements })),
  //       // NOTE: selectedRowId and notifySelectedRowValidity are specific to the
  //       // selected row feature (rowSelectionEnabled) -- other CTs do not use them.
  //       // Two things are needed:
  //       //   1. Backend: accept selectedRowId and return selectedRowInResults: boolean — true if
  //       //      that row exists anywhere in the full filtered set, not just the current page.
  //       //   2. Frontend: after the fetch resolves, call
  //       //      comparisonToolService.notifySelectedRowValidity(response.selectedRowInResults).
  //       //      Add this inside the unpinnedFetch$ result callback in ComparisonToolService
  //       //      (comparison-tool.service.ts:221).
  //     ),
  //   );
  // }
  //
  // getPinnedData(pinnedItems: string[], sortMeta: SortMeta[]) {
  //   this.comparisonToolService.fetchPinned(
  //     this.eqtlService
  //       .getEqtlData({ items: pinnedItems, itemFilterType: 'Include', ...sortMeta })
  //       .pipe(map((response) => ({ data: response.data, totalCount: response.page.totalElements }))),
  //   );
  // }
}
