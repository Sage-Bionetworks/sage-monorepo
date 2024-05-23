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
import { ScrollerOptions } from 'primeng/api';
import { MatSnackBar } from '@angular/material/snack-bar';

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
  @Input({ required: true }) optionsPerPage!: number;
  @Input({ required: true }) selectedOptions!: any[];
  @Input({ required: true }) placeholder = 'Search items';
  @Input({ required: true }) showAvatar!: boolean | undefined;
  @Input({ required: true }) filterByApiClient!: boolean | undefined;
  @Input({ required: false }) lazy = true;
  @Input({ required: false }) showLoader = false;
  @Input({ required: false }) itemHeight = 50;

  @Output() selectionChange = new EventEmitter<any[]>();
  @Output() searchChange = new EventEmitter<string>();
  @Output() pageChange = new EventEmitter<number>();

  constructor(private _snackBar: MatSnackBar) {}

  overlayOptions = {
    showTransitionOptions: '0ms',
    hideTransitionOptions: '0ms',
  };

  searchTerm = '';
  filter = true;

  isLoading = false;
  loadedPages = new Set();
  delays = 400;
  scrollerOptions!: ScrollerOptions;

  ngOnInit(): void {
    this.scrollerOptions = {
      delay: this.showLoader ? this.delays : 0, // if no loader is applied, load data seamlessly
      showLoader: this.showLoader,
      step: this.optionsPerPage,
      scrollHeight: `${this.itemHeight * this.optionsPerPage + 12}px`, // set height
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
    const validOptionValues = new Set(
      this.options.map((option) => option.value)
    );

    // check if every selected option is valid
    const allValid = this.selectedOptions.every(
      (option) =>
        option !== null && option !== undefined && validOptionValues.has(option)
    );

    if (!allValid) {
      // if any option is not valid, show an error message and return -1
      this.openSnackBar(
        'Unable to get the challenges. Please refresh the page and try again.'
      );
      // this.openSnackBar('Error: One or more selected options are invalid.');
      return -1;
    } else {
      // If all options are valid, just return the length of selectedOptions
      return this.selectedOptions.length;
    }
  }

  onLazyLoad(event: MultiSelectLazyLoadEvent) {
    // note: virtual scroll needs to be set 'true' to enable lazy load
    // trigger loader animation every time lazy load initiated
    const startPage = Math.floor(event.first / this.optionsPerPage);
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

  onCustomSearch(): void {
    if (this.lazy) {
      this.loadedPages.clear();
      this.triggerLoading();
    }
    this.searchChange.emit(this.searchTerm);
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

  openSnackBar(message: string) {
    this._snackBar.open(message, undefined, {
      duration: 30000,
    });
  }
}
