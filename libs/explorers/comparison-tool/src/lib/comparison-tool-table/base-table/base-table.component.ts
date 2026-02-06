import { Component, computed, inject, input, ViewEncapsulation } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { CommaSeparatePipe } from '@sagebionetworks/explorers/util';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { ComparisonToolTableLinkComponent } from '../comparison-tool-table-link/comparison-tool-table-link.component';
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
  readonly comparisonToolService = inject(ComparisonToolService);

  selectedColumns = this.comparisonToolService.selectedColumns;
  viewConfig = this.comparisonToolService.viewConfig;
  totalRecords = this.comparisonToolService.totalResultsCount;
  first = this.comparisonToolService.first;
  isHeatmapCircleClickable = computed(
    () => !!this.comparisonToolService.viewConfig().heatmapCircleClickTransformFn,
  );

  data = input.required<Record<string, any>[]>();
  shouldPaginate = input<boolean>(true);
  shouldShowNoDataMessage = input<boolean>(true);
  columnWidth = input<string>('auto');

  paginationConfig: PaginationOptions = {
    rows: 10,
    showCurrentPageReport: true,
    currentPageReportTemplate: '{first}-{last} of {totalRecords}',
    showPageLinks: true,
    paginatorStyleClass: 'comparison-tool-paginator',
  };

  onLazyLoad(event: TableLazyLoadEvent) {
    this.comparisonToolService.handleLazyLoad(event);
  }

  onHeatmapCircleClick(rowData: unknown, cellData: unknown, columnKey: string, event: Event): void {
    this.comparisonToolService.showHeatmapDetailsPanel(rowData, cellData, columnKey, event);
  }
}
