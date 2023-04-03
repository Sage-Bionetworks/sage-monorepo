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
  @Output() lazyLoad = new EventEmitter<LazyLoadEvent>();

  overlayOptions = {
    showTransitionOptions: '0ms',
    hideTransitionOptions: '0ms',
  };

  searchTerm = '';
  filter = true;

  ngOnInit(): void {
    this.showAvatar = this.showAvatar ? this.showAvatar : false;
    this.filter = this.filterByApiClient ? !this.filterByApiClient : false;
  }

  onSearch(searched: any): void {
    console.log(searched);
    this.searchChange.emit(searched.filter);
  }

  onChange(selected: string[]): void {
    this.selectionChange.emit(selected);
  }

  onLazyLoad(event: LazyLoadEvent): void {
    // setTimeout(() => {
    console.log(event);
    this.lazyLoad.emit(event);
    // }, 1000);
  }

  getAvatar(value: FilterValue): Avatar {
    return {
      name: value.label || '',
      src: value.avatarUrl ? value.avatarUrl : '',
      size: 32,
    };
  }
}
