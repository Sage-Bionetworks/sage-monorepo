import {
  AfterContentInit,
  Component,
  OnDestroy,
  OnInit,
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
} from '@sagebionetworks/openchallenges/api-client-angular';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { Filter, FilterValue } from '@sagebionetworks/openchallenges/ui';
import {
  challengeStartYearRangeFilter,
  challengeStatusFilter,
  challengeSubmissionTypesFilter,
  challengeInputDataTypesFilter,
  challengeIncentivesFilter,
  challengePlatformsFilter,
  challengeOrganizationsFilter,
} from './challenge-search-filters';
import { challengeSortFilterValues } from './challenge-search-filters-values';
import {
  BehaviorSubject,
  Observable,
  Subject,
  forkJoin,
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
import { Calendar } from 'primeng/calendar';
import { DatePipe } from '@angular/common';
import { union } from 'lodash';
import { DateRange } from './date-range';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoinConcurrent } from '@sagebionetworks/openchallenges/util';

@Component({
  selector: 'openchallenges-challenge-search',
  templateUrl: './challenge-search.component.html',
  styleUrls: ['./challenge-search.component.scss'],
})
export class ChallengeSearchComponent
  implements OnInit, AfterContentInit, OnDestroy
{
  public appVersion: string;
  datePipe: DatePipe = new DatePipe('en-US');

  private query: BehaviorSubject<ChallengeSearchQuery> =
    new BehaviorSubject<ChallengeSearchQuery>({});

  // set a default behaviorSubject to trigger searchTearm's changes
  private searchTerms: BehaviorSubject<string> = new BehaviorSubject<string>(
    ''
  );

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
  sortedBy!: string;

  // set default values
  defaultSelectedYear = undefined;
  defaultSortedBy = 'relevance';
  defaultPageNumber = 0;
  defaultPageSize = 24;

  // define filters
  sortFilters: FilterValue[] = challengeSortFilterValues;
  startYearRangeFilter: Filter = challengeStartYearRangeFilter;

  // checkbox filters
  statusFilter = challengeStatusFilter;
  submissionTypesFilter = challengeSubmissionTypesFilter;
  incentivesFilter = challengeIncentivesFilter;

  // dropdown filters
  platformsFilter = challengePlatformsFilter;
  inputDataTypesFilter = challengeInputDataTypesFilter;
  organizationsFilter = challengeOrganizationsFilter;

  // define selected filter values
  selectedStatus!: string[];
  selectedSubmissionTypes!: string[];
  selectedIncentives!: string[];
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
    private _snackBar: MatSnackBar
  ) {
    this.appVersion = this.configService.config.appVersion;
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
      this.selectedOrgs = this.splitParam(params['organizations']).map(
        (idString) => +idString
      );

      this.searchedTerms = params['searchTerms'];
      this.selectedPageNumber = +params['pageNumber'];
      this.selectedPageSize = +params['pageSize'];
      this.sortedBy = params['sort'];

      const defaultQuery = {
        pageNumber: this.selectedPageNumber || this.defaultPageNumber,
        pageSize: this.selectedPageSize || this.defaultPageSize,
        sort: this.sortedBy || this.defaultSortedBy,
        searchTerms: this.searchedTerms,
        minStartDate: this.selectedMinStartDate,
        maxStartDate: this.selectedMaxStartDate,
        status: this.selectedStatus,
        submissionTypes: this.selectedSubmissionTypes,
        platforms: this.selectedPlatforms,
        incentives: this.selectedIncentives,
        inputDataTypes: this.selectedInputDataTypes,
        organizations: this.selectedOrgs,
      } as ChallengeSearchQuery;

      this.query.next(defaultQuery);
    });

    // update the total number of challenges in database with empty query
    this.challengeService
      .listChallenges({} as ChallengeSearchQuery)
      .subscribe((page) => (this.totalChallengesCount = page.totalElements));

    // update platform filter values
    this.platformSearchTerms
      .pipe(
        debounceTime(400),
        distinctUntilChanged(),
        takeUntil(this.destroy),
        switchMap((searchTerm) =>
          this.challengePlatformService.listChallengePlatforms({
            searchTerms: searchTerm,
            sort: 'name',
          } as ChallengePlatformSearchQuery)
        )
      )
      .subscribe((page) => {
        const searchedPlatforms = page.challengePlatforms.map((platform) => ({
          value: platform.slug,
          label: platform.name,
          active: false,
        })) as FilterValue[];

        const selectedPlatformValues = searchedPlatforms.filter((value) =>
          this.selectedPlatforms.includes(value.value as string)
        );
        this.platformsFilter.values = union(
          searchedPlatforms,
          selectedPlatformValues
        ) as FilterValue[];
      });

    // update input data type filter values
    this.inputDataTypeSearchTerms
      .pipe(
        debounceTime(400),
        distinctUntilChanged(),
        takeUntil(this.destroy),
        switchMap((searchTerm) =>
          this.challengeInputDataTypeService.listChallengeInputDataTypes({
            searchTerms: searchTerm,
            sort: 'name',
          } as ChallengeInputDataTypeSearchQuery)
        )
      )
      .subscribe((page) => {
        const searchedInputDataTypes = page.challengeInputDataTypes.map(
          (dataType) => ({
            value: dataType.slug,
            label: dataType.name,
            active: false,
          })
        ) as FilterValue[];

        const selectedInputDataTypesValues = searchedInputDataTypes.filter(
          (value) => this.selectedInputDataTypes.includes(value.value as string)
        );
        this.inputDataTypesFilter.values = union(
          searchedInputDataTypes,
          selectedInputDataTypesValues
        ) as FilterValue[];
      });

    // update organization filter values
    this.organizationSearchTerms
      .pipe(
        debounceTime(400),
        distinctUntilChanged(),
        takeUntil(this.destroy),
        switchMap((searchTerm) =>
          this.organizationService.listOrganizations({
            searchTerms: searchTerm,
            sort: 'name',
          } as OrganizationSearchQuery)
        ),
        map((page) => page.organizations),
        switchMap((orgs) =>
          forkJoin({
            orgs: of(orgs),
            avatarUrls: forkJoinConcurrent(
              orgs.map((org) => this.getOrganizationAvatarUrl(org)),
              Infinity
            ) as unknown as Observable<(Image | undefined)[]>,
          })
        )
      )
      .subscribe(({ orgs, avatarUrls }) => {
        const searchedOrgs = orgs.map((org, index) => ({
          value: org.id,
          label: org.name,
          avatarUrl: avatarUrls[index]?.url,
          active: false,
        })) as FilterValue[];

        const selectedOrgValues = searchedOrgs.filter((value) =>
          this.selectedOrgs.includes(value.value as number)
        );
        this.organizationsFilter.values = union(
          searchedOrgs,
          selectedOrgValues
        ) as FilterValue[];
      });
  }

  ngAfterContentInit(): void {
    this.searchTerms
      .pipe(
        skip(1),
        debounceTime(400),
        distinctUntilChanged(),
        takeUntil(this.destroy)
      )
      .subscribe((searched) => {
        const searchedTerms = searched === '' ? undefined : searched;
        this.router.navigate([], {
          queryParamsHandling: 'merge',
          queryParams: { searchTerms: searchedTerms },
        });
      });

    this.query
      .pipe(
        tap((query) => console.log('List challenges', query)),
        switchMap((query) => this.challengeService.listChallenges(query)),
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

  splitParam(activeParam: string | undefined, by = ','): any[] {
    return activeParam ? activeParam.split(by) : [];
  }

  collapseParam(selectedParam: any, by = ','): string | undefined {
    return selectedParam.length === 0
      ? undefined
      : this.splitParam(selectedParam.toString()).join(by);
  }

  onSearchChange(): void {
    this.searchTerms.next(this.searchedTerms);
  }

  onYearChange(): void {
    this.refreshed = false;

    this.isCustomYear = (this.selectedYear as string) === 'custom';
    if (!this.isCustomYear) {
      const yearRange = this.selectedYear as DateRange | undefined;
      this.router.navigate([], {
        queryParamsHandling: 'merge',
        queryParams: {
          minStartDate: yearRange?.start ? yearRange.start : undefined,
          maxStartDate: yearRange?.end ? yearRange.end : undefined,
        },
      });

      // reset custom range
      this.customMonthRange = undefined;
    }
  }

  onCalendarChange(): void {
    this.isCustomYear = true;
    if (this.calendar) {
      this.router.navigate([], {
        queryParamsHandling: 'merge',
        queryParams: {
          minStartDate: this.datePipe.transform(
            this.calendar.value[0],
            'yyyy-MM-dd'
          ),
          maxStartDate: this.datePipe.transform(
            this.calendar.value[1],
            'yyyy-MM-dd'
          ),
        },
      });
    }
  }

  onStatusChange(selected: string[]): void {
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        status: this.collapseParam(selected),
      },
    });
  }

  onSubmissionTypesChange(selected: string[]): void {
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        submissionTypes: this.collapseParam(selected),
      },
    });
  }

  onIncentivesChange(selected: string[]): void {
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        incentives: this.collapseParam(selected),
      },
    });
  }

  onPlatformsChange(selected: string[]): void {
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        platforms: this.collapseParam(selected),
      },
    });
  }

  onPlatformSearchChange(searched: string): void {
    this.platformSearchTerms.next(searched);
  }

  onInputDataTypesChange(selected: string[]): void {
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        inputDataTypes: this.collapseParam(selected),
      },
    });
  }

  onInputDataTypeSearchChange(searched: string): void {
    this.inputDataTypeSearchTerms.next(searched);
  }

  onOrganizationsChange(selected: number[]): void {
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        organizations: this.collapseParam(selected),
      },
    });
  }

  onOrganizationSearchChange(searched: string): void {
    this.organizationSearchTerms.next(searched);
  }

  onSortChange(): void {
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        sort: this.sortedBy,
      },
    });
  }

  onPageChange(event: any) {
    console.log(event);
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        pageNumber: event.page,
        pageSize: event.rows,
      },
    });
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, undefined, {
      duration: 30000,
    });
  }

  private getOrganizationAvatarUrl(
    org: Organization
  ): Observable<Image | undefined> {
    if (org.avatarKey && org.avatarKey.length > 0) {
      return this.imageService.getImage({
        objectKey: org.avatarKey,
        height: ImageHeight._32px,
        aspectRatio: ImageAspectRatio._11,
      } as ImageQuery);
    } else {
      return of(undefined);
    }
  }
}
