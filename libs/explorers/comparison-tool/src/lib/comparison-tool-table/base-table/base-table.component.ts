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
    showPageLinks: false,
    paginatorStyleClass: 'comparison-tool-paginator',
  };

  onLazyLoad(event: TableLazyLoadEvent) {
    this.comparisonToolService.handleLazyLoad(event);
  }

  getLinkText(cellData: any, column: any): string {
    if (typeof cellData === 'string') {
      // TODO: remove this logic in (MG-596)
      // Data should be part of an object but in the current data release may be a string
      // This should be removed once Gene Expression data is updated to use objects
      return cellData;
    }
    if (typeof cellData === 'object' && cellData !== null) {
      return cellData.link_text || column.link_text || '';
    }
    return column.link_text || '';
  }

  getLinkUrl(cellData: any, column: any): string | string[] {
    if (typeof cellData === 'string') {
      // For simple string values, if no link_url is provided in column config,
      // construct a link to the model details page (for internal links)
      // TODO: remove this logic in (MG-596)
      // Data should be part of an object but in the current data release may be a string
      // This should be removed once Gene Expression data is updated to use objects
      if (!column.link_url && column.type === 'link_internal') {
        // Return as array of path segments - Angular Router will encode properly
        return ['/models', cellData];
      }
      return column.link_url || '';
    }
    if (typeof cellData === 'object' && cellData !== null) {
      return cellData.link_url || column.link_url || '';
    }
    return column.link_url || '';
  }

  onHeatmapCircleClick(rowData: unknown, cellData: unknown, columnKey: string, event: Event): void {
    this.comparisonToolService.showHeatmapDetailsPanel(rowData, cellData, columnKey, event);
  }
}
