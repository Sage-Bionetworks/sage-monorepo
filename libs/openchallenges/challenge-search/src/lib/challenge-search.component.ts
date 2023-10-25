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
  ChallengePlatformService,
  ChallengePlatformSearchQuery,
  ChallengeInputDataTypeService,
  ChallengeInputDataTypeSearchQuery,
  ImageService,
  OrganizationService,
  OrganizationSearchQuery,
  Organization,
  Image,
  ImageQuery,
  ImageHeight,
  ImageAspectRatio,
  ChallengeSort,
  ChallengeStatus,
  ChallengeSubmissionType,
  ChallengeIncentive,
  ChallengePlatformSort,
  ChallengeInputDataTypeSort,
  OrganizationSort,
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
import {
  BehaviorSubject,
  Observable,
  Subject,
  forkJoin,
  iif,
  map,
  of,
  switchMap,
  throwError,
} from 'rxjs';
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
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';
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

  // set a default behaviorSubject to trigger searchTearm's changes
  private allSearchTerms = new BehaviorSubject<{
    challenge: string;
    platform: string;
    organization: string;
  }>({
    challenge: '',
    platform: '',
    organization: '',
  });

  private challengeSearchTerms: BehaviorSubject<string> =
    new BehaviorSubject<string>('');

  private platformSearchTerms: BehaviorSubject<string> =
    new BehaviorSubject<string>('');

  private inputDataTypeSearchTerms: BehaviorSubject<string> =
    new BehaviorSubject<string>('');

  private organizationSearchTerms: BehaviorSubject<string> =
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
    private router: Router,
    private challengeService: ChallengeService,
    private challengePlatformService: ChallengePlatformService,
    private challengeInputDataTypeService: ChallengeInputDataTypeService,
    private organizationService: OrganizationService,
    private imageService: ImageService,
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

      const isDateDefined = params['minStartDate'] || params['maxStartDate'];

      if (isDateDefined) {
        if (this.refreshed) {
          // display custom range only once with defined date query after refreshing
          this.selectedYear = 'custom';
          this.isCustomYear = true;
          const yearRange = [
            params['minStartDate']
              ? new Date(params['minStartDate'])
              : undefined,
            params['maxStartDate']
              ? new Date(params['maxStartDate'])
              : undefined,
          ];

          this.customMonthRange = yearRange as Date[];
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
      this.selectedPageSize = +params['pageSize'] || this.defaultPageSize;
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
      .listChallenges({})
      .subscribe((page) => (this.totalChallengesCount = page.totalElements));

    // update platform filter values
    this.platformSearchTerms
      .pipe(
        debounceTime(400),
        distinctUntilChanged(),
        takeUntil(this.destroy),
        switchMap((searchTerm: string) => {
          const sortedBy: ChallengePlatformSort = 'name';
          const platformQuery: ChallengePlatformSearchQuery = {
            searchTerms: searchTerm,
            sort: sortedBy,
          };
          return this.challengePlatformService.listChallengePlatforms(
            platformQuery
          );
        })
      )
      .subscribe((page) => {
        const searchedPlatforms = page.challengePlatforms.map((platform) => ({
          value: platform.slug,
          label: platform.name,
          active: false,
        })) as Filter[];

        const selectedPlatformValues = searchedPlatforms.filter((option) =>
          this.selectedPlatforms.includes(option.value as string)
        );
        this.platformsFilter.options = union(
          searchedPlatforms,
          selectedPlatformValues
        ) as Filter[];
      });

    // update input data type filter values
    this.inputDataTypeSearchTerms
      .pipe(
        debounceTime(400),
        distinctUntilChanged(),
        takeUntil(this.destroy),
        switchMap((searchTerm: string) => {
          const sortedBy: ChallengeInputDataTypeSort = 'name';
          const inputDataTypeQuery: ChallengeInputDataTypeSearchQuery = {
            searchTerms: searchTerm,
            sort: sortedBy,
          };
          return this.challengeInputDataTypeService.listChallengeInputDataTypes(
            inputDataTypeQuery
          );
        })
      )
      .subscribe((page) => {
        const searchedInputDataTypes = page.challengeInputDataTypes.map(
          (dataType) => ({
            value: dataType.slug,
            label: dataType.name,
            active: false,
          })
        ) as Filter[];

        const selectedInputDataTypesValues = searchedInputDataTypes.filter(
          (option) =>
            this.selectedInputDataTypes.includes(option.value as string)
        );
        this.inputDataTypesFilter.options = union(
          searchedInputDataTypes,
          selectedInputDataTypesValues
        ) as Filter[];
      });

    // update organization filter values
    this.organizationSearchTerms
      .pipe(
        debounceTime(400),
        distinctUntilChanged(),
        takeUntil(this.destroy),
        switchMap((searchTerm: string) => {
          const sortBy: OrganizationSort = 'name';
          const orgQuery: OrganizationSearchQuery = {
            searchTerms: searchTerm,
            sort: sortBy,
          };
          return this.organizationService.listOrganizations(orgQuery);
        }),
        map((page) => page.organizations),
        switchMap((orgs) =>
          forkJoin({
            orgs: of(orgs),
            avatarUrls: forkJoinConcurrent(
              orgs.map((org) => this.getOrganizationAvatarUrl(org)),
              Infinity
            ),
          })
        )
      )
      .subscribe(({ orgs, avatarUrls }) => {
        const searchedOrgs = orgs.map((org, index) => ({
          value: org.id,
          label: org.name,
          avatarUrl: avatarUrls[index]?.url,
          active: false,
        })) as Filter[];

        const selectedOrgValues = searchedOrgs.filter((option) =>
          this.selectedOrgs.includes(option.value as number)
        );
        this.organizationsFilter.options = union(
          searchedOrgs,
          selectedOrgValues
        ) as Filter[];
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
        this.onParamChange({ organizations: searched });
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
        minStartDate: yearRange?.start ? yearRange.start : undefined,
        maxStartDate: yearRange?.end ? yearRange.end : undefined,
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
    // update params of URL
    const params = Object.entries(filteredQuery)
      .map(([key, value]) => [key, this.collapseParam(value as FilterValue)])
      .reduce((obj, [key, value]) => obj.append(key, value), new HttpParams());
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
        this.platformSearchTerms.next(searched);
        break;
      case 'organizations':
        this.organizationSearchTerms.next(searched);
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

  private getOrganizationAvatarUrl(org: Organization): Observable<Image> {
    return iif(
      () => !!org.avatarKey,
      this.imageService.getImage({
        objectKey: org.avatarKey,
        height: ImageHeight._32px,
        aspectRatio: ImageAspectRatio._11,
      } as ImageQuery),
      of({ url: '' })
    ).pipe(
      catchError(() => {
        console.error(
          'Unable to get the image url. Please check the logs of the image service.'
        );
        return of({ url: '' });
      })
    );
  }
}
