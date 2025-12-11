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
  // Clone multiSortMeta for PrimeNG binding to prevent it from mutating the service state
  // The service will perform additional deep cloning in setSort() for immutability
  multiSortMeta = computed(() =>
    this.comparisonToolService.multiSortMeta().map((s) => ({ field: s.field, order: s.order })),
  );

  sortCallback(event: SortEvent) {
    // Shallow clone the array for signal change detection
    // The service's setSort() will deep clone the objects to ensure immutability
    const sortMeta = event.multiSortMeta ? [...event.multiSortMeta] : [];
    this.comparisonToolService.setSort(sortMeta);
    return [];
  }
}
