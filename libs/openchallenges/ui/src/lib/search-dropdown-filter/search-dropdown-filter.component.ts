import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FilterValue } from '../checkbox-filter/filter-value.model';
import { Avatar } from '../avatar/avatar';

@Component({
  selector: 'openchallenges-search-dropdown-filter',
  templateUrl: './search-dropdown-filter.component.html',
  styleUrls: ['./search-dropdown-filter.component.scss'],
})
export class SearchDropdownFilterComponent {
  @Input() values!: FilterValue[];
  @Input() selectedValues!: string[];
  @Input() placeholder = 'Search items';
  @Input() showAvatar = true;
  @Output() dropdownChange = new EventEmitter<string[]>();

  onChange(selected: string[]): void {
    this.dropdownChange.emit(selected);
  }

  getAvatar(value: FilterValue): Avatar {
    return {
      name: value.label || '',
      src: value.avatarUrl ? value.avatarUrl : '',
      size: 24,
    };
  }
}
