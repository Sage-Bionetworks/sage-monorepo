import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FilterValue } from '../checkbox-filter/filter-value.model';
import { Avatar } from '../avatar/avatar';
import { LazyLoadEvent } from 'primeng/api';

@Component({
  selector: 'openchallenges-search-dropdown-filter',
  templateUrl: './search-dropdown-filter.component.html',
  styleUrls: ['./search-dropdown-filter.component.scss'],
})
export class SearchDropdownFilterComponent implements OnInit {
  @Input() values!: FilterValue[];
  @Input() selectedValues!: string[];
  @Input() placeholder = 'Search items';
  @Input() showAvatar!: boolean | undefined;
  @Input() filterByApiClient!: boolean | undefined;
  @Output() selectionChange = new EventEmitter<string[]>();
  @Output() searchChange = new EventEmitter<string>();
  @Output() lazyLoad = new EventEmitter<any>();

  overlayOptions = {
    showTransitionOptions: '0ms',
    hideTransitionOptions: '0ms',
  };

  // virtual scroll works as expected
  // when scroll item size (height) is specified
  scrollItemHeight = 40;

  searchTerm = '';
  filter = true;

  ngOnInit(): void {
    this.showAvatar = this.showAvatar ? this.showAvatar : false;

    if (this.filterByApiClient) {
      // if search field will be updated with query results
      // disable default filter and use custom search bar
      this.filter = !this.filterByApiClient;
    }

    if (this.showAvatar) {
      // if avatar displayed, increase item size height
      this.scrollItemHeight = 100;
    }
  }

  onSearch(event: any): void {
    this.searchChange.emit(event.filter);
  }

  onCustomSearch(): void {
    this.searchChange.emit(this.searchTerm);
  }

  onLazyLoad(event: LazyLoadEvent): void {
    if (event && event.last && event.first) {
      const currentSize = event.last - event.first + 1;
      const currentNumber = Math.floor(event.first / currentSize);

      this.lazyLoad.emit({ pageNumber: currentNumber, pageSize: currentSize });
    }
  }

  onChange(selected: string[]): void {
    this.selectionChange.emit(selected);
  }

  getAvatar(value: FilterValue): Avatar {
    return {
      name: value.label || '',
      src: value.avatarUrl ? value.avatarUrl : '',
      size: 32,
    };
  }
}
