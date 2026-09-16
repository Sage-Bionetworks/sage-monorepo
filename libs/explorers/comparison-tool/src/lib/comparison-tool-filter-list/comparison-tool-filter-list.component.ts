import { Component, inject, ViewEncapsulation } from '@angular/core';
import {
  ComparisonToolFilter,
  ComparisonToolFilterOption,
} from '@sagebionetworks/explorers/models';
import { ComparisonToolFilterService } from '@sagebionetworks/explorers/services';
import { FilterChicletComponent } from '@sagebionetworks/explorers/ui';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-comparison-tool-filter-list',
  imports: [FilterChicletComponent, SvgIconComponent],
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
    this.comparisonToolFilterService.clearAllFilters();
  }

  clearSelectedFilter(filter: ComparisonToolFilter, option: ComparisonToolFilterOption) {
    this.comparisonToolFilterService.setFilterOptionSelected(
      filter.query_param_key,
      option.label,
      false,
    );
  }

  removeSignificanceThresholdFilter(): void {
    this.comparisonToolFilterService.setSignificanceThresholdActive(false);
  }
}
