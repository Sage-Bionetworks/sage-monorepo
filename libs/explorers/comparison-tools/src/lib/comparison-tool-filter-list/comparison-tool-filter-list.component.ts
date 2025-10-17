import { Component, EventEmitter, Input, Output, ViewEncapsulation } from '@angular/core';
import {
  ComparisonToolFilter,
  ComparisonToolFilterOption,
} from '@sagebionetworks/explorers/models';
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
  /* Filters ------------------------------------------------------------------ */
  @Input() filters: ComparisonToolFilter[] = [] as ComparisonToolFilter[];
  @Output() changeEvent: EventEmitter<object> = new EventEmitter<object>();

  /* Significance Threshold --------------------------------------------------- */
  @Input() significanceThresholdActive = false;
  @Input() significanceThreshold = -1;
  @Output() onremoveSignificanceThresholdFilter: EventEmitter<any> = new EventEmitter();

  /* ----------------------------------------------------------------------- */
  /* All
  /* ----------------------------------------------------------------------- */
  shouldShowList() {
    return this.hasSelectedFilters() || this.significanceThresholdActive;
  }

  clearList() {
    this.removeSignificanceThresholdFilter();
    this.clearSelectedFilters();
  }

  /* ----------------------------------------------------------------------- */
  /* Filters
  /* ----------------------------------------------------------------------- */
  hasSelectedFilters() {
    for (const filter of this.filters) {
      if (filter.options.filter((option) => option.selected).length > 0) {
        return true;
      }
    }
    return false;
  }

  clearSelectedFilters(option?: ComparisonToolFilterOption) {
    if (option) {
      option.selected = false;
    } else {
      for (const filter of this.filters) {
        for (const o of filter.options) {
          o.selected = false;
        }
      }
    }
    this.changeEvent.emit(this.filters);
  }

  /* ----------------------------------------------------------------------- */
  /* Significance Threshold
  /* ----------------------------------------------------------------------- */
  removeSignificanceThresholdFilter(): void {
    this.significanceThresholdActive = false;
    this.onremoveSignificanceThresholdFilter.emit(this.significanceThresholdActive);
  }
}
