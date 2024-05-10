import {
  AfterContentInit,
  Component,
  OnDestroy,
  OnInit,
  Renderer2,
  ViewChild,
} from '@angular/core';
import {
  Challenge,
  ChallengeCategory,
  ChallengeIncentive,
  ChallengePlatformSort,
  ChallengeSearchQuery,
  ChallengeService,
  ChallengeSort,
  ChallengeStatus,
  ChallengeSubmissionType,
  EdamSection,
  OrganizationSort,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
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
import { Calendar, CalendarModule } from 'primeng/calendar';
import { CommonModule, DatePipe, Location } from '@angular/common';
import { assign, union } from 'lodash';
import { DateRange } from './date-range';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { DividerModule } from 'primeng/divider';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { PanelModule } from 'primeng/panel';
import { RadioButtonModule } from 'primeng/radiobutton';
import { SeoService } from '@sagebionetworks/shared/util';
import { getSeoData } from './challenge-search-seo-data';
import { HttpParams } from '@angular/common/http';
import { ChallengeSearchDataService } from './challenge-search-data.service';
import { MultiSelectLazyLoadEvent } from 'primeng/multiselect';

@Component({
  selector: 'openchallenges-challenge-search',
  standalone: true,
  imports: [
    CalendarModule,
    ChallengeCardComponent,
    CheckboxFilterComponent,
    CommonModule,
    DividerModule,
    DropdownModule,
    FooterComponent,
    FormsModule,
    InputTextModule,
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
export class ChallengeSearchComponent
  implements OnInit, AfterContentInit, OnDestroy
{
  public appVersion: string;
  public dataUpdatedOn: string;
  public privacyPolicyUrl: string;
  public termsOfUseUrl: string;
  public apiDocsUrl: string;
  datePipe: DatePipe = new DatePipe('en-US');

  private query: BehaviorSubject<ChallengeSearchQuery> =
    new BehaviorSubject<ChallengeSearchQuery>({});

  private challengeSearchTerms: BehaviorSubject<string> =
    new BehaviorSubject<string>('');

  private destroy = new Subject<void>();

  @ViewChild('calendar') calendar?: Calendar;
  @ViewChild('paginator', { static: false }) paginator!: PaginatorComponent;

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

  // dropdown filters
  inputDataTypesFilter = challengeInputDataTypesFilterPanel;
  organizationsFilter = challengeOrganizationsFilterPanel;
  platformsFilter = challengePlatformsFilterPanel;

  // define selected filter values

  selectedCategories!: ChallengeCategory[];
  selectedIncentives!: ChallengeIncentive[];
  selectedInputDataTypes!: number[];
  selectedOrgs!: number[];
  selectedPlatforms!: string[];
  selectedStatus!: ChallengeStatus[];
  selectedSubmissionTypes!: ChallengeSubmissionType[];

  constructor(
    private activatedRoute: ActivatedRoute,
    private challengeService: ChallengeService,
    private challengeSearchDataService: ChallengeSearchDataService,
    private readonly configService: ConfigService,
    private _snackBar: MatSnackBar,
    private seoService: SeoService,
    private renderer2: Renderer2,
    private _location: Location
  ) {
    this.appVersion = this.configService.config.appVersion;
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
    this.privacyPolicyUrl = this.configService.config.privacyPolicyUrl;
    this.termsOfUseUrl = this.configService.config.termsOfUseUrl;
    this.apiDocsUrl = this.configService.config.apiDocsUrl;
    this.seoService.setData(getSeoData(), this.renderer2);
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe((params) => {
      // Chunk of codes below used to update selected values that represent in the UI of filters
      this.selectedMinStartDate = params['minStartDate'];
      this.selectedMaxStartDate = params['maxStartDate'];

      if (params['minStartDate'] || params['maxStartDate']) {
        if (this.refreshed) {
          // display custom range only once with defined date query after refreshing
          this.selectedYear = 'custom';
          this.isCustomYear = true;
          this.customMonthRange = [
            new Date(params['minStartDate']),
            new Date(params['maxStartDate']),
          ];
          this.refreshed = false;
        }
      } else {
        // ensure to select default year range if no date defined
        this.selectedYear = this.defaultSelectedYear;
      }

      // update selected filter values based on params in url
      this.searchedTerms = params['searchTerms'];
      this.selectedCategories = this.splitParam(params['categories']);
      this.selectedIncentives = this.splitParam(params['incentives']);
      this.selectedInputDataTypes = this.splitParam(
        params['inputDataTypes']
      ).map((idString) => +idString);
      this.selectedOrgs = this.splitParam(params['organizations']).map(
        (idString) => +idString
      );
      this.selectedPageNumber = +params['pageNumber'] || this.defaultPageNumber;
      this.selectedPageSize = this.defaultPageSize; // no available pageSize options for users
      this.selectedPlatforms = this.splitParam(params['platforms']);
      this.selectedStatus = this.splitParam(params['status']);
      this.selectedSubmissionTypes = this.splitParam(params['submissionTypes']);
      this.sortedBy = params['sort'] || this.defaultSortedBy;

      const defaultQuery: ChallengeSearchQuery = {
        categories: this.selectedCategories,
        incentives: this.selectedIncentives,
        inputDataTypes: this.selectedInputDataTypes,
        maxStartDate: this.selectedMaxStartDate,
        minStartDate: this.selectedMinStartDate,
        organizations: this.selectedOrgs,
        pageNumber: this.selectedPageNumber,
        pageSize: this.selectedPageSize,
        platforms: this.selectedPlatforms,
        searchTerms: this.searchedTerms,
        sort: this.sortedBy,
        status: this.selectedStatus,
        submissionTypes: this.selectedSubmissionTypes,
      };

      this.query.next(defaultQuery);
    });

    // update the total number of challenges in database with empty query
    this.challengeService
      .listChallenges({ pageSize: 10000, pageNumber: 0 })
      .subscribe((page) => {
        // const cc = page.challenges.filter(
        //   (c) => c.inputDataTypes !== undefined && c.inputDataTypes.length > 0
        // );
        // console.log(cc.map((c) => c.inputDataTypes.map));
        this.totalChallengesCount = page.totalElements;
      });

    // this.challengeSearchDataService
    //   .searchEdamConcepts()
    //   .pipe(takeUntil(this.destroy))
    //   .subscribe((options) => {
    //     const selectedInputDataTypesValues = options.filter((option) =>
    //       this.selectedInputDataTypes.includes(option.value as number)
    //     );
    //     this.inputDataTypesFilter.options = union(
    //       options,
    //       selectedInputDataTypesValues
    //     );
    //   });

    // update organization filter values
    // this.challengeSearchDataService
    //   .searchOriganizations()
    //   .pipe(takeUntil(this.destroy))
    //   .subscribe((options) => {
    //     const selectedOrgValues = options.filter((option) =>
    //       this.selectedOrgs.includes(option.value as number)
    //     );
    //     this.organizationsFilter.options = union(options, selectedOrgValues);
    //   });

    // update platform filter values
    // this.challengeSearchDataService
    //   .getPlatforms({})
    //   .pipe(takeUntil(this.destroy))
    //   .subscribe((options) => {
    //     const selectedPlatformValues = options.filter((option) =>
    //       this.selectedPlatforms.includes(option.value as string)
    //     );
    //     this.platformsFilter.options = union(options, selectedPlatformValues);
    //   });
  }

  ngAfterContentInit(): void {
    this.challengeSearchTerms
      .pipe(
        skip(1),
        debounceTime(400),
        distinctUntilChanged(),
        takeUntil(this.destroy)
      )
      .subscribe((searched) => {
        this.onParamChange({ searchTerms: searched });
      });

    this.query
      .pipe(
        tap((query) => console.log('List challenges query', query)),
        switchMap((query: ChallengeSearchQuery) =>
          this.challengeService.listChallenges(query)
        ),
        tap((page) => console.log('List of challenges: ', page.challenges)),
        catchError((err) => {
          if (err.message) {
            this.openSnackBar(
              'Unable to get the challenges. Please refresh the page and try again.'
            );
          }
          return throwError(() => new Error(err.message));
        })
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
    if (this.calendar) {
      this.onParamChange({
        minStartDate: this.dateToFormat(this.calendar.value[0]),
        maxStartDate: this.dateToFormat(this.calendar.value[1]),
      });
    }
  }

  onParamChange(filteredQuery: any): void {
    // reset pagination settings when filters change
    if (!filteredQuery.pageNumber && !filteredQuery.pageSize) {
      filteredQuery.pageNumber = this.defaultPageNumber;
      filteredQuery.pageSize = this.defaultPageSize;
      // this.selectedPageSize = this.defaultPageSize;
      this.paginator.resetPageNumber();
    }
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
        (params, [key, value]) =>
          value !== '' ? params.set(key, value) : params.delete(key),
        currentParams
      );
    this._location.replaceState(location.pathname, params.toString());

    // update query to trigger API call
    const newQuery = assign(this.query.getValue(), filteredQuery);
    this.query.next(newQuery);
  }

  edamLoadedPages: Set<number> = new Set();
  platformsLoadedPages: Set<number> = new Set();
  organizationLoadedPages: Set<number> = new Set();

  onLazyLoadEdam(event: MultiSelectLazyLoadEvent): void {
    const size = 10; // default page size
    const startPage = Math.floor(event.first / size);
    const endPage = Math.floor(event.last / size);

    for (let page = startPage; page <= endPage; page++) {
      // check if the page has already been loaded to avoid duplicates
      if (!this.edamLoadedPages.has(page)) {
        this.edamLoadedPages.add(page);

        this.challengeSearchDataService
          .getEdamConcepts({
            pageNumber: page,
            pageSize: size,
            sections: [EdamSection.Data],
          })
          .pipe(takeUntil(this.destroy))
          .subscribe((newOptions) => {
            // find the starting index for the new options in the overall existing options list
            const startIndex = page * size;
            let options = this.inputDataTypesFilter.options;
            // update dropdown options by inserting new data into correct position
            if (startIndex < options.length) {
              options.splice(startIndex, 0, ...newOptions);
            } else {
              options = options.concat(newOptions);
            }

            const selectedInputDataValues = options.filter((option) =>
              this.selectedInputDataTypes.includes(option.value as number)
            );
            this.inputDataTypesFilter.options = union(
              options,
              selectedInputDataValues
            );
          });
      }
    }
  }

  onLazyLoadPlatform(event: MultiSelectLazyLoadEvent): void {
    const size = 10; // default page size
    const startPage = Math.floor(event.first / size);
    const endPage = Math.floor(event.last / size);

    for (let page = startPage; page <= endPage; page++) {
      if (!this.platformsLoadedPages.has(page)) {
        this.platformsLoadedPages.add(page);
        this.challengeSearchDataService
          .getPlatforms({
            pageNumber: page,
            pageSize: size,
          })
          .pipe(takeUntil(this.destroy))
          .subscribe((newOptions) => {
            // find the starting index for the new options in the overall existing options list
            const startIndex = page * size;
            let options = this.platformsFilter.options;
            // update dropdown options by inserting new data into correct position
            if (startIndex < options.length) {
              options.splice(startIndex, 0, ...newOptions);
            } else {
              options = options.concat(newOptions);
            }

            const selectedPlatformValues = options.filter((option) =>
              this.selectedPlatforms.includes(option.value as string)
            );
            this.platformsFilter.options = union(
              options,
              selectedPlatformValues
            );
          });
      }
    }
  }

  onLazyLoadOrganization(event: MultiSelectLazyLoadEvent): void {
    const size = 10; // default page size
    const startPage = Math.floor(event.first / size);
    const endPage = Math.floor(event.last / size);

    for (let page = startPage; page <= endPage; page++) {
      // check if the page has already been loaded to avoid duplicates
      if (!this.organizationLoadedPages.has(page)) {
        this.organizationLoadedPages.add(page);

        this.challengeSearchDataService
          .getOriganizations({
            pageNumber: page,
            pageSize: size,
          })
          .pipe(takeUntil(this.destroy))
          .subscribe((newOptions) => {
            // find the starting index for the new options in the overall existing options list
            const startIndex = page * size;
            let options = this.organizationsFilter.options;
            // update dropdown options by inserting new data into correct position
            if (startIndex < options.length) {
              options.splice(startIndex, 0, ...newOptions);
            } else {
              options = options.concat(newOptions);
            }

            const selectedOrgValues = options.filter((option) =>
              this.selectedOrgs.includes(option.value as number)
            );
            this.organizationsFilter.options = union(
              options,
              selectedOrgValues
            );
          });
      }
    }
  }

  onSearchChange(
    searchType: 'challenges' | 'inputDataTypes' | 'organizations' | 'platforms',
    searched: string
  ): void {
    switch (searchType) {
      case 'challenges':
        this.challengeSearchTerms.next(searched);
        break;
      case 'inputDataTypes':
        this.challengeSearchDataService.setEdamConceptSearch({
          searchTerms: searched,
          sections: [EdamSection.Data],
        });
        break;
      case 'organizations':
        this.challengeSearchDataService.setOriganizationSearch({
          searchTerms: searched,
          sort: OrganizationSort.Name,
        });
        break;
      case 'platforms':
        this.challengeSearchDataService.setPlatformSearch({
          searchTerms: searched,
          sort: ChallengePlatformSort.Name,
        });
        break;
    }
  }

  dateToFormat(date: Date, format?: 'yyyy-MM-dd'): string | null {
    return this.datePipe.transform(date, format);
  }

  splitParam(activeParam: string | undefined, by = ','): any[] {
    return activeParam ? activeParam.split(by) : [];
  }

  collapseParam(selectedParam: FilterValue | FilterValue[], by = ','): string {
    return Array.isArray(selectedParam)
      ? selectedParam.map((item) => item?.toString()).join(by)
      : (selectedParam as string) ?? '';
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, undefined, {
      duration: 30000,
    });
  }
}
