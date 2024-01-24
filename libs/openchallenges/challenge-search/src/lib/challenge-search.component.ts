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
  ChallengeService,
  ChallengeSearchQuery,
  ChallengeSort,
  ChallengeStatus,
  ChallengeSubmissionType,
  ChallengeIncentive,
  ChallengeCategory,
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
  challengeStartYearRangeFilterPanel,
  challengeStatusFilterPanel,
  challengeSubmissionTypesFilterPanel,
  challengeInputDataTypesFilterPanel,
  challengeIncentivesFilterPanel,
  challengePlatformsFilterPanel,
  challengeOrganizationsFilterPanel,
  challengeCategoriesFilterPanel,
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

@Component({
  selector: 'openchallenges-challenge-search',
  standalone: true,
  imports: [
    CalendarModule,
    CommonModule,
    DividerModule,
    DropdownModule,
    InputTextModule,
    MatIconModule,
    MatSnackBarModule,
    RouterModule,
    FormsModule,
    PanelModule,
    RadioButtonModule,
    ReactiveFormsModule,
    FooterComponent,
    PaginatorComponent,
    ChallengeCardComponent,
    CheckboxFilterComponent,
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

  challenges: Challenge[] = [];
  totalChallengesCount = 0;
  searchResultsCount = 0;

  @ViewChild('calendar') calendar?: Calendar;
  customMonthRange!: Date[] | undefined;
  isCustomYear = false;
  refreshed = true;

  selectedYear!: DateRange | string | undefined;
  selectedMinStartDate!: string | undefined;
  selectedMaxStartDate!: string | undefined;
  searchedTerms!: string;
  selectedPageNumber!: number;
  selectedPageSize!: number;
  sortedBy!: ChallengeSort;

  // set default values
  defaultSelectedYear = undefined;
  defaultSortedBy: ChallengeSort = 'relevance';
  defaultPageNumber = 0;
  defaultPageSize = 24;
  @ViewChild('paginator', { static: false }) paginator!: PaginatorComponent;

  // define filters
  sortFilters: Filter[] = challengeSortFilter;
  startYearRangeFilter: FilterPanel = challengeStartYearRangeFilterPanel;

  // checkbox filters
  statusFilter = challengeStatusFilterPanel;
  submissionTypesFilter = challengeSubmissionTypesFilterPanel;
  incentivesFilter = challengeIncentivesFilterPanel;
  categoriesFilter = challengeCategoriesFilterPanel;

  // dropdown filters
  platformsFilter = challengePlatformsFilterPanel;
  inputDataTypesFilter = challengeInputDataTypesFilterPanel;
  organizationsFilter = challengeOrganizationsFilterPanel;

  // define selected filter values
  selectedStatus!: ChallengeStatus[];
  selectedSubmissionTypes!: ChallengeSubmissionType[];
  selectedIncentives!: ChallengeIncentive[];
  selectedCategories!: ChallengeCategory[];
  selectedPlatforms!: string[];
  selectedOrgs!: number[];
  selectedInputDataTypes!: string[];

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
    this.challengeService
      .listChallenges({ pageSize: 1000 })
      .subscribe((page) => {
        // update challenges and total number of results
        const challengesMisMatch = page.challenges
          .filter(
            (challenge) =>
              challenge.endDate &&
              new Date(challenge.endDate) < new Date() &&
              challenge.status !== 'completed'
          )
          .map((challenge) => ({
            name: challenge.name,
            startDate: challenge.startDate,
            endDate: challenge.endDate,
            status: challenge.status,
          }));
        const challengesNoBothDates = page.challenges
          .filter((challenge) => !challenge.startDate && !challenge.endDate)
          .map((challenge) => ({
            name: challenge.name,
            startDate: challenge.startDate,
            endDate: challenge.endDate,
            status: challenge.status,
          }));
        const challengesNoStartDate = page.challenges
          .filter((challenge) => !challenge.startDate && challenge.endDate)
          .map((challenge) => ({
            name: challenge.name,
            startDate: challenge.startDate,
            endDate: challenge.endDate,
            status: challenge.status,
          }));
        const challengesNoEndDate = page.challenges
          .filter((challenge) => challenge.startDate && !challenge.endDate)
          .map((challenge) => ({
            name: challenge.name,
            startDate: challenge.startDate,
            endDate: challenge.endDate,
            status: challenge.status,
          }));

        console.log('Challenges outdated');
        console.log(challengesMisMatch);
        console.log('Challenges provided no dates at all');
        console.log(challengesNoBothDates);
        console.log('Challenges provided startDate, but no endDate');
        console.log(challengesNoEndDate);
        console.log('Challenges provided endDate, but no startDate');
        console.log(challengesNoStartDate);
      });

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
      this.selectedStatus = this.splitParam(params['status']);
      this.selectedSubmissionTypes = this.splitParam(params['submissionTypes']);
      this.selectedIncentives = this.splitParam(params['incentives']);
      this.selectedPlatforms = this.splitParam(params['platforms']);
      this.selectedInputDataTypes = this.splitParam(params['inputDataTypes']);
      this.selectedCategories = this.splitParam(params['categories']);
      this.selectedOrgs = this.splitParam(params['organizations']).map(
        (idString) => +idString
      );
      this.searchedTerms = params['searchTerms'];
      this.selectedPageNumber = +params['pageNumber'] || this.defaultPageNumber;
      this.selectedPageSize = this.defaultPageSize; // no available pageSize options for users
      this.sortedBy = params['sort'] || this.defaultSortedBy;

      const defaultQuery: ChallengeSearchQuery = {
        pageNumber: this.selectedPageNumber,
        pageSize: this.selectedPageSize,
        sort: this.sortedBy,
        searchTerms: this.searchedTerms,
        minStartDate: this.selectedMinStartDate,
        maxStartDate: this.selectedMaxStartDate,
        status: this.selectedStatus,
        submissionTypes: this.selectedSubmissionTypes,
        platforms: this.selectedPlatforms,
        incentives: this.selectedIncentives,
        inputDataTypes: this.selectedInputDataTypes,
        categories: this.selectedCategories,
        organizations: this.selectedOrgs,
      };

      this.query.next(defaultQuery);
    });

    // update the total number of challenges in database with empty query
    this.challengeService
      .listChallenges({ pageSize: 1, pageNumber: 0 })
      .subscribe((page) => {
        this.totalChallengesCount = page.totalElements;

        // const num = page.challenges.filter((c) => c.startDate !== null).length;
        // console.log(num);
      });

    // update platform filter values
    this.challengeSearchDataService
      .searchPlatforms()
      .pipe(takeUntil(this.destroy))
      .subscribe((options) => {
        const selectedPlatformValues = options.filter((option) =>
          this.selectedPlatforms.includes(option.value as string)
        );
        this.platformsFilter.options = union(options, selectedPlatformValues);
      });

    // update input data type filter values
    this.challengeSearchDataService
      .searchOriganizations()
      .pipe(takeUntil(this.destroy))
      .subscribe((options) => {
        const selectedInputDataTypesValues = options.filter((option) =>
          this.selectedInputDataTypes.includes(option.value as string)
        );
        this.inputDataTypesFilter.options = union(
          options,
          selectedInputDataTypesValues
        );
      });

    // update organization filter values
    this.challengeSearchDataService
      .searchOriganizations()
      .pipe(takeUntil(this.destroy))
      .subscribe((options) => {
        const selectedOrgValues = options.filter((option) =>
          this.selectedOrgs.includes(option.value as number)
        );
        this.organizationsFilter.options = union(options, selectedOrgValues);
      });
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
        const challengesNoStartDate = page.challenges
          .filter((challenge) => !challenge.startDate && challenge.endDate)
          .map((challenge) => ({
            name: challenge.name,
            startDate: challenge.startDate,
            endDate: challenge.endDate,
            status: challenge.status,
          }));
        const challengesNoEndDate = page.challenges
          .filter((challenge) => challenge.startDate && !challenge.endDate)
          .map((challenge) => ({
            name: challenge.name,
            startDate: challenge.startDate,
            endDate: challenge.endDate,
            status: challenge.status,
          }));
        console.log('Challenges provided startDate, but no endDate');
        console.log(challengesNoStartDate);
        console.log('Challenges provided endDate, but no startDate');
        console.log(challengesNoEndDate);

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

  onSearchChange(
    searchType: 'challenges' | 'platforms' | 'organizations',
    searched: string
  ): void {
    switch (searchType) {
      case 'challenges':
        this.challengeSearchTerms.next(searched);
        break;
      case 'platforms':
        this.challengeSearchDataService.setPlatformSearchTerms(searched);
        break;
      case 'organizations':
        this.challengeSearchDataService.setOriganizationSearchTerms(searched);
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
