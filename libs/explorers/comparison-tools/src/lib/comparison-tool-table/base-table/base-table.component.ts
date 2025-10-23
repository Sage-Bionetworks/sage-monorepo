import { Component, inject, input, output, ViewEncapsulation } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { CommaSeparatePipe } from '@sagebionetworks/explorers/util';
import { SortEvent } from 'primeng/api';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { ComparisonToolTableLinkComponent } from '../../comparison-tool-table-link/comparison-tool-table-link.component';
import { HeatmapCircleComponent } from '../heatmap-circle/heatmap-circle.component';
import { PrimaryIdentifierControlsComponent } from '../primary-identifier-controls/primary-identifier-controls.component';

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
    PrimaryIdentifierControlsComponent,
  ],
  templateUrl: './base-table.component.html',
  styleUrls: ['./base-table.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class BaseTableComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);

  currentConfig = this.comparisonToolService.currentConfig;

  data = input.required<Record<string, any>[]>();
  shouldPaginate = input<boolean>(true);
  shouldShowNoDataMessage = input<boolean>(true);
  viewDetailsTooltip = input<string>('View detailed results');
  viewDetailsEvent = output<string>();

  columnWidth = 'auto';
  paginationConfig: PaginationOptions = {
    rows: 10,
    showCurrentPageReport: true,
    currentPageReportTemplate: '{first}-{last} of {totalRecords}',
    showPageLinks: false,
    paginatorStyleClass: 'comparison-tool-paginator',
  };

  sortCallback(event: SortEvent) {
    this.comparisonToolService.setSort(event.field, event.order);
  }
}
