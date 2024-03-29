import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { Filter } from '../checkbox-filter/filter.model';
import { Avatar } from '../avatar/avatar';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AvatarComponent } from '../avatar/avatar.component';
import { MultiSelectModule } from 'primeng/multiselect';

@Component({
  selector: 'openchallenges-search-dropdown-filter',
  standalone: true,
  imports: [AvatarComponent, CommonModule, FormsModule, MultiSelectModule],
  templateUrl: './search-dropdown-filter.component.html',
  styleUrls: ['./search-dropdown-filter.component.scss'],
})
export class SearchDropdownFilterComponent implements OnInit {
  @Input({ required: true }) options!: Filter[];
  @Input({ required: true }) selectedOptions!: any[];
  @Input({ required: true }) placeholder = 'Search items';
  @Input({ required: true }) showAvatar!: boolean | undefined;
  @Input({ required: true }) filterByApiClient!: boolean | undefined;
  @Output() selectionChange = new EventEmitter<any[]>();
  @Output() searchChange = new EventEmitter<string>();

  overlayOptions = {
    showTransitionOptions: '0ms',
    hideTransitionOptions: '0ms',
  };

  searchTerm = '';
  filter = true;

  ngOnInit(): void {
    this.showAvatar = this.showAvatar ? this.showAvatar : false;

    if (this.filterByApiClient) {
      // if search field will be updated with query results
      // disable default filter and use custom search bar
      this.filter = !this.filterByApiClient;
    }
  }

  onSearch(event: any): void {
    this.searchChange.emit(event.filter);
  }

  onCustomSearch(): void {
    this.searchChange.emit(this.searchTerm);
  }

  onChange(selected: string[] | number[]): void {
    // this filter will emit as string anyways
    this.selectionChange.emit(selected);
  }

  getAvatar(option: Filter): Avatar {
    return {
      name: option.label ?? '',
      src: option.avatarUrl ? option.avatarUrl : '',
      size: 32,
    };
  }
}
