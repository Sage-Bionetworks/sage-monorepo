import { Component, Input, Output, EventEmitter, ViewEncapsulation } from '@angular/core';
import { GCTFilter, GCTFilterOption } from '@sagebionetworks/agora/models';

import { FilterListItemComponent } from '../filter-list-item/filter-list-item.component';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-filter-list',
  imports: [FilterListItemComponent, SvgIconComponent],
  templateUrl: './filter-list.component.html',
  styleUrls: ['./filter-list.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class FilterListComponent {
  /* Filters ------------------------------------------------------------------ */
  @Input() filters: GCTFilter[] = [] as GCTFilter[];
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

  clearSelectedFilters(option?: GCTFilterOption) {
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
