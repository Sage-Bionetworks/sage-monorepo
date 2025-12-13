import { Component, computed, inject, input, ViewEncapsulation } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { SortEvent } from 'primeng/api';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'explorers-comparison-tool-columns',
  imports: [TableModule, TooltipModule],
  templateUrl: './comparison-tool-columns.component.html',
  styleUrls: ['./comparison-tool-columns.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolColumnsComponent {
  readonly comparisonToolService = inject(ComparisonToolService);

  columnWidth = input<string>('auto');

  selectedColumns = this.comparisonToolService.selectedColumns;
  currentConfig = this.comparisonToolService.currentConfig;
  resultsCount = this.comparisonToolService.totalResultsCount;
  /**
   * Clone multiSortMeta for PrimeNG binding to prevent it from mutating the service state.
   * PrimeNG's Table component directly mutates the multiSortMeta array when users interact
   * with column sorting (known PrimeNG behavior). Without cloning, these mutations would
   * corrupt the service's immutable state.
   */
  multiSortMeta = computed(() =>
    this.comparisonToolService.multiSortMeta().map((s) => ({ field: s.field, order: s.order })),
  );

  sortCallback(event: SortEvent) {
    // Shallow clone the array to ensure signal change detection triggers properly
    const sortMeta = event.multiSortMeta ? [...event.multiSortMeta] : [];
    this.comparisonToolService.setSort(sortMeta);
    return [];
  }
}
