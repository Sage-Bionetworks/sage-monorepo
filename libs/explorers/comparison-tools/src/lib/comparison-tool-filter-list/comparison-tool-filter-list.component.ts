import { Component, inject, ViewEncapsulation } from '@angular/core';
import { ComparisonToolFilterOption } from '@sagebionetworks/explorers/models';
import { ComparisonToolFilterService } from '@sagebionetworks/explorers/services';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { ComparisonToolFilterListItemComponent } from './comparison-tool-filter-list-item/comparison-tool-filter-list-item.component';

@Component({
  selector: 'explorers-comparison-tool-filter-list',
  imports: [ComparisonToolFilterListItemComponent, SvgIconComponent],
  templateUrl: './comparison-tool-filter-list.component.html',
  styleUrls: ['./comparison-tool-filter-list.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolFilterListComponent {
  private readonly comparisonToolFilterService = inject(ComparisonToolFilterService);

  filters = this.comparisonToolFilterService.filters;
  hasSelectedFilters = this.comparisonToolFilterService.hasSelectedFilters;
  significanceThresholdActive = this.comparisonToolFilterService.significanceThresholdActive;
  significanceThreshold = this.comparisonToolFilterService.significanceThreshold;

  shouldShowList() {
    return this.hasSelectedFilters() || this.significanceThresholdActive();
  }

  clearList() {
    this.removeSignificanceThresholdFilter();
    this.clearSelectedFilters();
  }

  clearSelectedFilters(option?: ComparisonToolFilterOption) {
    if (option) {
      option.selected = false;
    } else {
      for (const filter of this.filters()) {
        for (const o of filter.options) {
          o.selected = false;
        }
      }
    }
    this.comparisonToolFilterService.setFilters(this.filters());
  }

  removeSignificanceThresholdFilter(): void {
    this.comparisonToolFilterService.setSignificanceThresholdActive(false);
  }
}
