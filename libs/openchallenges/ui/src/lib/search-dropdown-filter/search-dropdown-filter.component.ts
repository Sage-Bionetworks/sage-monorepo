import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { Filter } from '../checkbox-filter/filter.model';
import { Avatar } from '../avatar/avatar';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AvatarComponent } from '../avatar/avatar.component';
import {
  MultiSelectLazyLoadEvent,
  MultiSelectModule,
} from 'primeng/multiselect';
import { SkeletonModule } from 'primeng/skeleton';

@Component({
  selector: 'openchallenges-search-dropdown-filter',
  standalone: true,
  imports: [
    AvatarComponent,
    CommonModule,
    FormsModule,
    MultiSelectModule,
    SkeletonModule,
  ],
  templateUrl: './search-dropdown-filter.component.html',
  styleUrls: ['./search-dropdown-filter.component.scss'],
})
export class SearchDropdownFilterComponent implements OnInit {
  @Input({ required: true }) options!: Filter[];
  @Input({ required: false }) optionsPerPage = 10;
  @Input({ required: true }) selectedOptions!: any[];
  @Input({ required: true }) placeholder = 'Search items';
  @Input({ required: true }) showAvatar!: boolean | undefined;
  @Input({ required: true }) filterByApiClient!: boolean | undefined;
  @Input({ required: false }) lazy = true;

  @Output() selectionChange = new EventEmitter<any[]>();
  @Output() searchChange = new EventEmitter<string>();
  @Output() lazyLoad = new EventEmitter<any>();
  @Output() loadedPageChange = new EventEmitter<number>();

  overlayOptions = {
    showTransitionOptions: '0ms',
    hideTransitionOptions: '0ms',
  };

  scrollOptions = {
    delay: 250,
    showLoader: true,
  };

  searchTerm = '';
  filter = true;

  loadedPages = new Set();

  ngOnInit(): void {
    this.showAvatar = this.showAvatar ? this.showAvatar : false;

    // make sure the input page size is valid
    this.optionsPerPage =
      Number.isInteger(this.optionsPerPage) && this.optionsPerPage >= 0
        ? this.optionsPerPage
        : 10;

    if (this.filterByApiClient) {
      // if search field will be updated with query results
      // disable default filter and use custom search bar
      this.filter = !this.filterByApiClient;
    }
  }

  onLazyLoad(event: MultiSelectLazyLoadEvent): void {
    // note: virtual scroll needs to be set 'true' to enable lazy load
    // the loader animation is triggered every time lazy load initiated
    const startPage = Math.floor(event.first / this.optionsPerPage);
    const endPage = Math.floor(event.last / this.optionsPerPage);

    for (let page = startPage; page <= endPage; page++) {
      if (!this.loadedPages.has(page)) {
        this.loadedPages.add(page);
        this.loadedPageChange.emit(page);
      }
    }
  }

  clearLoadedPages(): void {
    this.loadedPages.clear();
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
