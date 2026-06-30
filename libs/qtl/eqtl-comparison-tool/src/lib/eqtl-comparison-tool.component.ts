import { Component, inject } from '@angular/core';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  ComparisonToolService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { SidebarComponent } from './components/sidebar/sidebar.component';

@Component({
  selector: 'qtl-eqtl-comparison-tool',
  imports: [ComparisonToolComponent, SidebarComponent],
  providers: [
    MessageService,
    ...provideComparisonToolService(),
    ...provideComparisonToolFilterService(),
  ],
  templateUrl: './eqtl-comparison-tool.component.html',
  styleUrls: ['./eqtl-comparison-tool.component.scss'],
})
export class EqtlComparisonToolComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);

  // TODO(QTL-113): uncomment and populate when CT data fetching is implemented
  // viewConfig: Partial<ComparisonToolViewConfig> = {
  //   rowSelectionEnabled: true,
  //   rowHoverEnabled: true,
  //   rowIdDataKey: 'some_id_field',
  //   // ... other config
  // };

  constructor() {
    // TODO(QTL-113): uncomment when CT data fetching is implemented
    // this.comparisonToolService.setViewConfig(this.viewConfig);
    this.comparisonToolService.connect({
      config$: of([]),
      queryParams$: of({}),
    });
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
  //   this.comparisonToolService.startFetch();
  //   this.eqtlService.getEqtlData({ ...query, selectedRowId })
  //     .pipe(takeUntilDestroyed(this.destroyRef))
  //     .subscribe({
  //       next: (response) => {
  //         this.comparisonToolService.setUnpinnedData(response.data);
  //         this.comparisonToolService.totalResultsCount.set(response.page.totalElements);
  //         // NOTE: selectedRowId and notifySelectedRowValidity are specific to the
  //         // selected row feature (rowSelectionEnabled) -- other CTs do not use them.
  //         // The backend must check whether selectedRowId exists anywhere in the full
  //         // filtered result set (not just the current page) and return
  //         // selectedRowInResults: boolean in the response.
  //         this.comparisonToolService.notifySelectedRowValidity(response.selectedRowInResults);
  //       },
  //       error: () => {
  //         this.comparisonToolService.setUnpinnedData([]);
  //         this.comparisonToolService.totalResultsCount.set(0);
  //       },
  //     });
  // }
  //
  // getPinnedData(pinnedItems: string[], sortMeta: SortMeta[]) {
  //   this.comparisonToolService.startFetch();
  //   this.eqtlService.getEqtlData({ items: pinnedItems, itemFilterType: 'Include', ...sortMeta })
  //     .pipe(takeUntilDestroyed(this.destroyRef))
  //     .subscribe({
  //       next: (response) => {
  //         this.comparisonToolService.setPinnedData(response.data);
  //         this.comparisonToolService.pinnedResultsCount.set(response.data.length);
  //       },
  //       error: () => {
  //         this.comparisonToolService.setPinnedData([]);
  //         this.comparisonToolService.pinnedResultsCount.set(0);
  //       },
  //     });
  // }
}
