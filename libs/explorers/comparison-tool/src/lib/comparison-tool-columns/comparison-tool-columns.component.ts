import { Component, inject, input, ViewEncapsulation } from '@angular/core';
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
  private readonly comparisonToolService = inject(ComparisonToolService);

  columnWidth = input<string>('auto');

  selectedColumns = this.comparisonToolService.selectedColumns;
  currentConfig = this.comparisonToolService.currentConfig;
  resultsCount = this.comparisonToolService.totalResultsCount;

  sortCallback(event: SortEvent) {
    this.comparisonToolService.setSort(event.field, event.order);
  }
}
