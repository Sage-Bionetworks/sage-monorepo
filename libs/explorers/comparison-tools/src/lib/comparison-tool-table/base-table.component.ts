import { Component, inject, input, ViewEncapsulation } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { CommaSeparatePipe } from '@sagebionetworks/explorers/util';
import { SortEvent } from 'primeng/api';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { ComparisonToolTableLinkComponent } from '../comparison-tool-table-link/comparison-tool-table-link.component';
import { HeatmapCircleComponent } from './heatmap-circle/heatmap-circle.component';

interface PaginationOptions {
  rows: number;
  showCurrentPageReport: boolean;
  currentPageReportTemplate: string;
  showPageLinks: boolean;
  paginatorStyleClass: string;
}

@Component({
  selector: 'explorers-base-table',
  imports: [
    TooltipModule,
    TableModule,
    HeatmapCircleComponent,
    CommaSeparatePipe,
    ComparisonToolTableLinkComponent,
  ],
  templateUrl: './base-table.component.html',
  styleUrls: ['./base-table.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class BaseTableComponent {
  comparisonToolService = inject(ComparisonToolService);

  currentConfig = this.comparisonToolService.currentConfig;

  data = input.required<Record<string, any>[]>();
  shouldPaginate = input<boolean>(true);

  columnWidth = 'auto';
  paginationConfig: PaginationOptions = {
    rows: 10,
    showCurrentPageReport: true,
    currentPageReportTemplate: '{first}-{last} of {totalRecords}',
    showPageLinks: false,
    paginatorStyleClass: 'comparison-tool-paginator',
  };

  sortCallback(event: SortEvent) {
    // TODO: implement Sort (MG-459)
  }
}
