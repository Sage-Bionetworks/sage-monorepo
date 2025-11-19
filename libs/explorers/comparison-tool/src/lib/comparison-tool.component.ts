import { Component, inject, input, model, output } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { TableLazyLoadEvent } from 'primeng/table';
import { ComparisonToolControlsComponent } from './comparison-tool-controls/comparison-tool-controls.component';
import { ComparisonToolFilterListComponent } from './comparison-tool-filter-list/comparison-tool-filter-list.component';
import { ComparisonToolFilterPanelComponent } from './comparison-tool-filter-panel/comparison-tool-filter-panel.component';
import { ComparisonToolHeaderComponent } from './comparison-tool-header/comparison-tool-header.component';
import { ComparisonToolTableComponent } from './comparison-tool-table/comparison-tool-table.component';
import { HelpLinksComponent } from './help-links/help-links.component';

@Component({
  selector: 'explorers-comparison-tool',
  imports: [
    LoadingContainerComponent,
    ComparisonToolHeaderComponent,
    ComparisonToolFilterPanelComponent,
    ComparisonToolControlsComponent,
    ComparisonToolFilterListComponent,
    ComparisonToolTableComponent,
    HelpLinksComponent,
  ],
  templateUrl: './comparison-tool.component.html',
  styleUrls: ['./comparison-tool.component.scss'],
})
export class ComparisonToolComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);

  isLoading = input(true);

  lazyLoad = output<TableLazyLoadEvent>();

  currentConfig = this.comparisonToolService.currentConfig;
  loadingResultsCount = this.comparisonToolService.loadingResultsCount;

  isFilterPanelOpen = model(false);

  toggleFilterPanel() {
    this.isFilterPanelOpen.update((isOpen) => !isOpen);
  }

  onLazyLoad(event: TableLazyLoadEvent) {
    this.lazyLoad.emit(event);
  }
}
