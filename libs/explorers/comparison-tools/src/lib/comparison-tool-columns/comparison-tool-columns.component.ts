import { AfterViewInit, Component, inject, ViewChild } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { Table, TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'explorers-comparison-tool-columns',
  imports: [TableModule, TooltipModule],
  templateUrl: './comparison-tool-columns.component.html',
  styleUrls: ['./comparison-tool-columns.component.scss'],
})
export class ComparisonToolColumnsComponent implements AfterViewInit {
  private readonly comparisonToolService = inject(ComparisonToolService);

  currentConfig = this.comparisonToolService.currentConfig;
  resultsCount = this.comparisonToolService.totalResultsCount;
  columnWidth = 'auto';

  @ViewChild('headerTable') headerTable!: Table;

  ngAfterViewInit() {
    this.headerTable.onFilter.subscribe(() => this.updateResultsCount());
    this.headerTable.onSort.subscribe(() => this.updateSort());
    // set the initial count
    this.updateResultsCount();
  }

  updateResultsCount() {
    if (this.headerTable) {
      if (this.headerTable.filteredValue) {
        this.resultsCount.set(this.headerTable.filteredValue.length);
      } else if (this.headerTable.value) {
        this.resultsCount.set(this.headerTable.value.length);
      } else {
        this.resultsCount.set(0);
      }
    }
  }

  updateSort() {
    // TODO implement
  }
}
