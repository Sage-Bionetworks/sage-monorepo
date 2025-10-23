import { Component, inject, ViewEncapsulation } from '@angular/core';
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

  columns = this.comparisonToolService.columns;
  currentConfig = this.comparisonToolService.currentConfig;
  resultsCount = this.comparisonToolService.totalResultsCount;
  columnWidth = 'auto';

  sortCallback(event: SortEvent) {
    this.comparisonToolService.setSort(event.field, event.order);
  }
}
