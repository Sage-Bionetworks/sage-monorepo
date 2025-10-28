import { Component, inject, ViewEncapsulation } from '@angular/core';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { TooltipModule } from 'primeng/tooltip';
import { ComparisonToolColumnsComponent } from '../comparison-tool-columns/comparison-tool-columns.component';
import { BaseTableComponent } from './base-table/base-table.component';

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
    // TODO
  }

  pinAll() {
    // TODO: this.comparisonToolService.pinAll();
  }

  clearAllPinned() {
    // TODO: this.comparisonToolService.clearAllPinned();
  }
}
