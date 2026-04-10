import { Component, computed, inject, ViewEncapsulation } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { Paginator, PaginatorState } from 'primeng/paginator';
import { HelpLinksComponent } from '../help-links/help-links.component';

@Component({
  selector: 'explorers-comparison-tool-footer',
  imports: [Paginator, HelpLinksComponent],
  templateUrl: './comparison-tool-footer.component.html',
  styleUrls: ['./comparison-tool-footer.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolFooterComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);

  shouldPaginate = computed(() => this.comparisonToolService.isInitialized());
  pageSize = this.comparisonToolService.pageSize;
  pageSizeOptions = [...this.comparisonToolService.pageSizeOptions];
  totalResultsCount = this.comparisonToolService.totalResultsCount;
  first = this.comparisonToolService.first;

  onPageChange(event: PaginatorState) {
    const pageSize = event.rows ?? this.comparisonToolService.pageSize();
    const pageNumber = event.first != null ? Math.floor(event.first / pageSize) : 0;
    this.comparisonToolService.updateQuery({ pageNumber, pageSize });
  }
}
