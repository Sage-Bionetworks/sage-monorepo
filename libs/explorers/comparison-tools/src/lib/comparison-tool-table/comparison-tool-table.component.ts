import { Component, inject, ViewEncapsulation } from '@angular/core';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { TooltipModule } from 'primeng/tooltip';
import { BaseTableComponent } from './base-table/base-table.component';
import { ComparisonToolColumnsComponent } from './comparison-tool-columns/comparison-tool-columns.component';

@Component({
  selector: 'explorers-comparison-tool-table',
  imports: [TooltipModule, ComparisonToolColumnsComponent, SvgIconComponent, BaseTableComponent],
  templateUrl: './comparison-tool-table.component.html',
  styleUrls: ['./comparison-tool-table.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolTableComponent {
  comparisonToolService = inject(ComparisonToolService);
  comparisonToolFilterService = inject(ComparisonToolFilterService);

  pinnedItems = this.comparisonToolService.pinnedItems;
  maxPinnedItems = this.comparisonToolService.maxPinnedItems;
  hasMaxPinnedItems = this.comparisonToolService.hasMaxPinnedItems;
  disabledPinTooltip = this.comparisonToolService.disabledPinTooltip;

  searchTerm = this.comparisonToolFilterService.searchTerm;
  hasSelectedFilters = this.comparisonToolFilterService.hasSelectedFilters;

  pinnedData = this.comparisonToolService.pinnedData;
  unpinnedData = this.comparisonToolService.unpinnedData;

  downloadPinnedCsv() {
    // TODO: MG-451
  }

  pinAll() {
    // TODO: handle pagination (i.e. unpinnedData only contains the first page of data, rather than all unpinned data)
    this.comparisonToolService.pinList(this.unpinnedData().map((item) => item._id));
  }

  clearAllPinned() {
    this.comparisonToolService.resetPinnedItems();
  }
}
