import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FilterValue } from '../checkbox-filter/filter-value.model';
import { Avatar } from '../avatar/avatar';

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
  @Output() selectionChange = new EventEmitter<string[]>();
  @Output() searchChange = new EventEmitter<string>();

  overlayOptions = {
    showTransitionOptions: '0ms',
    hideTransitionOptions: '0ms',
  };

  searchTerm = '';

  ngOnInit(): void {
    this.showAvatar = this.showAvatar ? this.showAvatar : false;
  }

  onSearch(searched: any): void {
    console.log(searched);
    this.searchChange.emit(searched.filter);
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
