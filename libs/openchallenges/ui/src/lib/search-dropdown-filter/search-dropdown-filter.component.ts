import { Avatar } from '../avatar/avatar';
import { AvatarComponent } from '../avatar/avatar.component';
import { CheckboxModule } from 'primeng/checkbox';
import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { Filter } from '../checkbox-filter/filter.model';
import { FormsModule } from '@angular/forms';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputTextModule } from 'primeng/inputtext';
import { MultiSelectLazyLoadEvent, MultiSelectModule } from 'primeng/multiselect';
import { ScrollerOptions } from 'primeng/api';
import { SkeletonModule } from 'primeng/skeleton';

@Component({
  selector: 'openchallenges-search-dropdown-filter',
  imports: [
    AvatarComponent,
    CheckboxModule,
    CommonModule,
    InputGroupModule,
    InputTextModule,
    FormsModule,
    MultiSelectModule,
    SkeletonModule,
  ],
  templateUrl: './search-dropdown-filter.component.html',
  styleUrls: ['./search-dropdown-filter.component.scss'],
})
export class SearchDropdownFilterComponent implements OnInit {
  @Input({ required: true }) options!: Filter[];
  @Input({ required: true }) optionsPerPage!: number;
  @Input({ required: true }) selectedOptions!: any[];
  @Input({ required: true }) showAvatar!: boolean | undefined;
  @Input({ required: true }) filterByApiClient!: boolean | undefined;
  @Input({ required: false }) lazy = true;
  @Input({ required: false }) optionHeight = 40; // height of each option
  @Input({ required: false }) optionSize = 10; // total number of displaying options
  @Input({ required: false }) placeholder = 'Search items';
  @Input({ required: false }) showLoader = false;

  @Output() selectionChange = new EventEmitter<any[]>();
  @Output() searchChange = new EventEmitter<string>();
  @Output() pageChange = new EventEmitter<number>();

  overlayOptions = {
    showTransitionOptions: '0ms',
    hideTransitionOptions: '0ms',
  };

  searchTerm = '';
  filter = true;
  isAllOptionsSelected = false;

  isLoading = false;
  loadedPages = new Set();
  delays = 400;
  scrollerOptions!: ScrollerOptions;

  ngOnInit(): void {
    this.scrollerOptions = {
      delay: this.showLoader ? this.delays : 0, // if no loader is applied, load data seamlessly
      showLoader: this.showLoader,
      step: this.optionsPerPage,
      scrollHeight: `${this.optionHeight * this.optionSize + 12}px`, // dynamically set scroller height
    };

    this.showAvatar = this.showAvatar ? this.showAvatar : false;

    if (this.filterByApiClient) {
      // if search field will be updated with query results
      // disable default filter and use custom search bar
      this.filter = !this.filterByApiClient;
    }
  }

  get validSelectionCount(): number {
    if (!this.options) {
      return 0;
    }

    // preparing a set for quick lookup
    const validOptionValues = new Set(this.options.map((option) => option.value));

    // count how many selected values
    // exlude the invalid selected values if they are not in the option list
    return this.selectedOptions.filter(
      (option) => option !== null && option !== undefined && validOptionValues.has(option),
    ).length;
  }

  onLazyLoad(event: MultiSelectLazyLoadEvent) {
    // note: virtual scroll needs to be set 'true' to enable lazy load
    // trigger loader animation every time lazy load initiated
    const startPage = Math.max(0, Math.floor(event.first / this.optionsPerPage)); // avoid negative pages
    const endPage = Math.floor(event.last / this.optionsPerPage);

    // load next page as scrolling down
    for (let page = startPage; page <= endPage; page++) {
      if (!this.loadedPages.has(page)) {
        this.loadedPages.add(page);
        this.pageChange.emit(page);
      }
    }
  }

  onSearch(event: any): void {
    this.searchChange.emit(event.filter);
  }

  toggleAllOptions() {
    if (this.isAllOptionsSelected) {
      this.selectedOptions = this.options.map((option) => option.value);
    } else {
      this.selectedOptions = [];
    }
  }

  onCustomSearch(): void {
    if (this.lazy) {
      this.loadedPages.clear();
      this.triggerLoading();
    }
    this.searchChange.emit(this.searchTerm);
  }

  stopPropagation(event: Event): void {
    event.stopPropagation();
  }

  onChange(selected: string[] | number[]): void {
    // this filter will emit as string anyways
    this.selectionChange.emit(selected);
  }

  triggerLoading(): void {
    this.isLoading = true;
    setTimeout(() => {
      this.isLoading = false;
    }, this.delays);
  }

  getAvatar(option: Filter): Avatar {
    return {
      name: option.label ?? '',
      src: option.avatarUrl ? option.avatarUrl : '',
      size: 32,
    };
  }
}
