import {
  AfterContentInit,
  Component,
  inject,
  Input,
  OnDestroy,
  OnInit,
  Renderer2,
  ViewChild,
} from '@angular/core';
import {
  Challenge,
  ChallengeCategory,
  ChallengeIncentive,
  ChallengeSearchQuery,
  ChallengeService,
  ChallengeSort,
  ChallengeStatus,
  ChallengeSubmissionType,
  EdamSection,
} from '@sagebionetworks/openchallenges/api-client';
import { ConfigService } from '@sagebionetworks/openchallenges/web/angular/config';
import {
  ChallengeCardComponent,
  CheckboxFilterComponent,
  Filter,
  FilterPanel,
  FilterValue,
  FooterComponent,
  PaginatorComponent,
  SearchDropdownFilterComponent,
} from '@sagebionetworks/openchallenges/ui';
import {
  challengeCategoriesFilterPanel,
  challengeIncentivesFilterPanel,
  challengeInputDataTypesFilterPanel,
  challengeOperationsFilterPanel,
  challengeOrganizationsFilterPanel,
  challengePlatformsFilterPanel,
  challengeStartYearRangeFilterPanel,
  challengeStatusFilterPanel,
  challengeSubmissionTypesFilterPanel,
} from './challenge-search-filter-panels';
import { challengeSortFilter } from './challenge-search-filters';
import { BehaviorSubject, Subject, switchMap, throwError } from 'rxjs';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  skip,
  takeUntil,
  tap,
} from 'rxjs/operators';
import { DatePicker, DatePickerModule } from 'primeng/datepicker';
import { DatePipe, Location } from '@angular/common';
import { assign, isEqual, unionWith } from 'lodash';
import { DateRange } from './date-range';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { DividerModule } from 'primeng/divider';
import { DropdownModule } from 'primeng/dropdown';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { InputTextModule } from 'primeng/inputtext';
import { PanelModule } from 'primeng/panel';
import { RadioButtonModule } from 'primeng/radiobutton';
import { SeoService } from '@sagebionetworks/shared/util';
import { getSeoData } from './challenge-search-seo-data';
import { HttpParams } from '@angular/common/http';
import { ChallengeSearchDataService } from './challenge-search-data.service';
import { ChallengeSearchDropdown, CHALLENGE_SEARCH_DROPDOWNS } from './challenge-search-dropdown';

@Component({
  selector: 'openchallenges-challenge-search',
  imports: [
    ChallengeCardComponent,
    CheckboxFilterComponent,
    DatePickerModule,
    DividerModule,
    DropdownModule,
    FooterComponent,
    FormsModule,
    InputTextModule,
    InputGroupModule,
    InputGroupAddonModule,
    MatIconModule,
    MatSnackBarModule,
    PaginatorComponent,
    PanelModule,
    RadioButtonModule,
    ReactiveFormsModule,
    RouterModule,
    SearchDropdownFilterComponent,
  ],
  templateUrl: './challenge-search.component.html',
  styleUrls: ['./challenge-search.component.scss'],
})
export class ChallengeSearchComponent implements OnInit, AfterContentInit, OnDestroy {
  private readonly challengeService = inject(ChallengeService);
  private readonly challengeSearchDataService = inject(ChallengeSearchDataService);
  private readonly configService = inject(ConfigService);
  private readonly _snackBar = inject(MatSnackBar);
  private readonly seoService = inject(SeoService);
  private readonly renderer2 = inject(Renderer2);
  private readonly _location = inject(Location);

  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;
  public enableOperationFilter: boolean;
  datePipe: DatePipe = new DatePipe('en-US');

  private readonly query: BehaviorSubject<ChallengeSearchQuery> =
    new BehaviorSubject<ChallengeSearchQuery>({});

  private readonly challengeSearchTerms: BehaviorSubject<string> = new BehaviorSubject<string>('');

  private readonly destroy = new Subject<void>();

  @ViewChild('calendar') calendar?: DatePicker;
  @ViewChild('paginator', { static: false }) paginator!: PaginatorComponent;

  // retrieve route params using @Input
  @Input({ required: false }) categories!: ChallengeCategory[];
  @Input({ required: false }) incentives!: ChallengeIncentive[];
  @Input({ required: false }) inputDataTypes!: number[];
  @Input({ required: false }) maxStartDate!: string;
  @Input({ required: false }) minStartDate!: string;
  @Input({ required: false }) operations!: number[];
  @Input({ required: false }) organizations!: number[];
  @Input({ required: false }) pageNumber!: number;
  @Input({ required: false }) platforms!: string[];
  @Input({ required: false }) searchTerms!: string;
  @Input({ required: false }) sort!: ChallengeSort;
  @Input({ required: false }) status!: ChallengeStatus[];
  @Input({ required: false }) submissionTypes!: ChallengeSubmissionType[];

  challenges: Challenge[] = [];
  totalChallengesCount = 0;
  searchResultsCount!: number;

  customMonthRange!: Date[] | undefined;
  isCustomYear = false;
  refreshed = true;

  searchedTerms!: string;
  selectedMaxStartDate!: string | undefined;
  selectedMinStartDate!: string | undefined;
  selectedPageNumber!: number;
  selectedPageSize!: number;
  selectedYear!: DateRange | string | undefined;
  sortedBy!: ChallengeSort;

  // set default values
  defaultPageNumber = 0;
  defaultPageSize = 24;
  defaultDropdownOptionSize = 50;
  defaultSelectedYear = undefined;
  defaultSortedBy: ChallengeSort = 'relevance';

  // define filters
  sortFilters: Filter[] = challengeSortFilter;
  startYearRangeFilter: FilterPanel = challengeStartYearRangeFilterPanel;

  // checkbox filters
  categoriesFilter = challengeCategoriesFilterPanel;
  incentivesFilter = challengeIncentivesFilterPanel;
  statusFilter = challengeStatusFilterPanel;
  submissionTypesFilter = challengeSubmissionTypesFilterPanel;

  // set dropdown filter placeholders
  dropdownFilters!: { [key: string]: FilterPanel };

  // define selected filter values
  selectedValues = {
    categories: [] as ChallengeCategory[],
    incentives: [] as ChallengeIncentive[],
    inputDataTypes: [] as number[],
    operations: [] as number[],
    organizations: [] as number[],
    platforms: [] as string[],
    status: [] as ChallengeStatus[],
    submissionTypes: [] as ChallengeSubmissionType[],
  };

  constructor() {
    this.appVersion = this.configService.config.app.version;
    this.dataUpdatedOn = this.configService.config.data.updatedOn;
    this.privacyPolicyUrl = this.configService.config.urls.privacyPolicy;
    this.termsOfUseUrl = this.configService.config.urls.termsOfUse;
    this.apiDocsUrl = this.configService.config.api.docs.url;
    this.enableOperationFilter = this.configService.config.features.operationFilter.enabled;
    this.seoService.setData(getSeoData(), this.renderer2);
  }

  ngOnInit() {
    this.updateSelectedValues();
    this.updateQuery();

    // update the total number of challenges in database with empty query
    this.challengeService.listChallenges().subscribe((page) => {
      this.totalChallengesCount = page.totalElements;
    });

    // update dropdown filters
    this.dropdownFilters = {
      inputDataTypes: challengeInputDataTypesFilterPanel,
      operations: challengeOperationsFilterPanel,
      organizations: challengeOrganizationsFilterPanel,
      platforms: challengePlatformsFilterPanel,
    };

    // load initial data and listen to fetch data as query changes
    this.loadInitialDropdownData();
  }

  ngAfterContentInit(): void {
    this.challengeSearchTerms
      .pipe(skip(1), debounceTime(400), distinctUntilChanged(), takeUntil(this.destroy))
      .subscribe((searched) => {
        this.onParamChange({ searchTerms: searched });
      });

    this.query
      .pipe(
        tap((query) => console.log('List challenges query', query)),
        switchMap((query: ChallengeSearchQuery) => this.challengeService.listChallenges(query)),
        tap((page) => console.log('List of challenges: ', page.challenges)),
        catchError((err) => {
          if (err.message) {
            this.openSnackBar(
              'Unable to get the challenges. Please refresh the page and try again.',
            );
          }
          return throwError(() => new Error(err.message));
        }),
      )
      .subscribe((page) => {
        // update challenges and total number of results
        this.searchResultsCount = page.totalElements;
        this.challenges = page.challenges;
      });
  }

  ngOnDestroy(): void {
    this.destroy.next();
    this.destroy.complete();
  }

  onYearChange(): void {
    this.refreshed = false;

    this.isCustomYear = (this.selectedYear as string) === 'custom';
    if (!this.isCustomYear) {
      const yearRange = this.selectedYear as DateRange | undefined;
      this.onParamChange({
        minStartDate: yearRange?.start,
        maxStartDate: yearRange?.end,
      });

      // reset custom range
      this.customMonthRange = undefined;
    }
  }

  onCalendarChange(): void {
    this.isCustomYear = true;
    if (this.calendar?.value) {
      const [start, end] = this.calendar.value;
      this.onParamChange({
        minStartDate: this.dateToFormat(start, 'yyyy-MM-dd'),
        maxStartDate: this.dateToFormat(end, 'yyyy-MM-dd'),
      });
    }
  }

  onParamChange(filteredQuery: any): void {
    // reset pagination settings when filters change
    if (!filteredQuery.pageNumber && !filteredQuery.pageSize) {
      filteredQuery.pageNumber = this.defaultPageNumber;
      filteredQuery.pageSize = this.defaultPageSize;
      this.paginator.resetPageNumber();
    }

    // update selected filter values if specific parameters change
    Object.keys(filteredQuery).forEach((key) => {
      if (key in this.selectedValues && filteredQuery[key] !== undefined) {
        (this.selectedValues as any)[key] = filteredQuery[key];
      }
    });

    // update params of URL
    const currentParams = new HttpParams({
      fromString: this._location.path().split('?')[1] ?? '',
    });
    const params = Object.entries(filteredQuery)
      .map(([key, value]) => {
        // avoid adding pageNumber and pageSize to the params if they are default
        if (
          (key === 'pageNumber' && value === this.defaultPageNumber) ||
          (key === 'pageSize' && value === this.defaultPageSize)
        ) {
          return [key, ''];
        }
        return [key, this.collapseParam(value as FilterValue)];
      })
      .reduce(
        // update with new param, or delete the param if empty string
        (params, [key, value]) => (value !== '' ? params.set(key, value) : params.delete(key)),
        currentParams,
      );
    this._location.replaceState(location.pathname, params.toString());

    // update query to trigger API call
    const newQuery = assign(this.query.getValue(), filteredQuery);
    this.query.next(newQuery);
  }

  onSearchChange(searchType: 'challenges' | ChallengeSearchDropdown, searched: string): void {
    if (searchType === 'challenges') {
      this.challengeSearchTerms.next(searched);
    } else {
      // reset options except selections when search term is applied
      const selectedOptions = this.dropdownFilters[searchType].options.filter((option) => {
        if (Array.isArray(option.value)) {
          return option.value.some((item) =>
            (this.selectedValues[searchType] as FilterValue[]).includes(item),
          );
        } else {
          return (this.selectedValues[searchType] as FilterValue[]).includes(option.value);
        }
      });
      this.dropdownFilters[searchType].options = selectedOptions;
      this.challengeSearchDataService.setSearchQuery(searchType, {
        searchTerms: searched,
      });
    }
  }

  onLazyLoad(dropdown: ChallengeSearchDropdown, page: number): void {
    const query: any = { pageNumber: page };

    if (page === 0) {
      // reset ids and slugs params of dropdown search query
      query.ids = undefined;
      query.slugs = undefined;
    }
    // load next page as scrolling down
    this.challengeSearchDataService.setSearchQuery(dropdown, query);
  }

  private updateSelectedValues() {
    this.selectedMinStartDate = this.minStartDate;
    this.selectedMaxStartDate = this.maxStartDate;

    if (this.minStartDate || this.maxStartDate) {
      if (this.refreshed) {
        // display custom range only once with defined date query after refreshing
        this.selectedYear = 'custom';
        this.isCustomYear = true;
        this.customMonthRange = [new Date(this.minStartDate), new Date(this.maxStartDate)];
        this.refreshed = false;
      }
    } else {
      // ensure to select default year range if no date defined
      this.selectedYear = this.defaultSelectedYear;
    }

    // update selected filter values based on params in url
    this.searchedTerms = this.searchTerms;
    this.selectedPageNumber = +this.pageNumber || this.defaultPageNumber;
    this.selectedPageSize = this.defaultPageSize; // no available pageSize options for users
    this.sortedBy = this.sort || this.defaultSortedBy;

    this.selectedValues['categories'] = this.splitParam(this.categories);
    this.selectedValues['incentives'] = this.splitParam(this.incentives);
    this.selectedValues['inputDataTypes'] = this.splitParam(this.inputDataTypes).map(
      (idString) => +idString,
    );
    this.selectedValues['operations'] = this.splitParam(this.operations).map(
      (idString) => +idString,
    );
    this.selectedValues['organizations'] = this.splitParam(this.organizations).map(
      (idString) => +idString,
    );
    this.selectedValues['platforms'] = this.splitParam(this.platforms);
    this.selectedValues['status'] = this.splitParam(this.status);
    this.selectedValues['submissionTypes'] = this.splitParam(this.submissionTypes);
  }

  private updateQuery() {
    const defaultQuery: ChallengeSearchQuery = {
      categories: this.selectedValues['categories'],
      incentives: this.selectedValues['incentives'],
      inputDataTypes: this.selectedValues['inputDataTypes'],
      maxStartDate: this.selectedMaxStartDate,
      minStartDate: this.selectedMinStartDate,
      operations: this.selectedValues['operations'],
      organizations: this.selectedValues['organizations'],
      pageNumber: this.selectedPageNumber,
      pageSize: this.selectedPageSize,
      platforms: this.selectedValues['platforms'],
      searchTerms: this.searchedTerms,
      sort: this.sortedBy,
      status: this.selectedValues['status'],
      submissionTypes: this.selectedValues['submissionTypes'],
    };

    this.query.next(defaultQuery);
  }

  private setDropdownSelections(): void {
    this.challengeSearchDataService.setSearchQuery('inputDataTypes', {
      ids: this.selectedValues['inputDataTypes'],
    });
    this.challengeSearchDataService.setSearchQuery('operations', {
      ids: this.selectedValues['operations'],
    });
    this.challengeSearchDataService.setSearchQuery('organizations', {
      ids: this.selectedValues['organizations'],
    });
    this.challengeSearchDataService.setSearchQuery('platforms', {
      slugs: this.selectedValues['platforms'],
    });
  }

  private loadInitialDropdownData(): void {
    // query the dropdown filter option(s) pre-selected in url param (only initially)
    this.setDropdownSelections();

    // fetch and update dropdown options with new data for each dropdown category
    CHALLENGE_SEARCH_DROPDOWNS.forEach((dropdown) => {
      const extraDefaultParams =
        dropdown === 'inputDataTypes'
          ? { sections: [EdamSection.Data] }
          : dropdown === 'operations'
            ? { sections: EdamSection.Operation }
            : {};

      this.challengeSearchDataService
        .fetchData(dropdown, {
          pageSize: this.defaultDropdownOptionSize, // set constant pageSize to match lazyLoad
          ...extraDefaultParams,
        })
        .pipe(takeUntil(this.destroy))
        .subscribe((newOptions) => {
          // update filter options by appending new unique filter values to the bottom
          this.dropdownFilters[dropdown].options = unionWith(
            this.dropdownFilters[dropdown].options,
            newOptions,
            isEqual,
          );
        });
    });
  }

  dateToFormat(date: Date, format?: 'yyyy-MM-dd'): string | null {
    return this.datePipe.transform(date, format);
  }

  splitParam(activeParam: any, by = ','): any[] {
    return activeParam ? activeParam.split(by) : [];
  }

  collapseParam(selectedParam: FilterValue | FilterValue[], by = ','): string {
    return Array.isArray(selectedParam)
      ? selectedParam.map((item) => item?.toString()).join(by)
      : ((selectedParam as string) ?? '');
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, undefined, {
      duration: 30000,
    });
  }
}
