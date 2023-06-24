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
  ChallengePlatformService,
  ChallengeSearchQuery,
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
  // challengeDifficultyFilter,
  challengeSubmissionTypesFilter,
  challengeInputDataTypesFilter,
  challengeIncentivesFilter,
  challengePlatformsFilter,
  challengeOrganizationsFilter,
  // challengeOrganizaterFilter,
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
import {
  // assign,
  union,
} from 'lodash';
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

  private organizationSearchTerms: BehaviorSubject<string> =
    new BehaviorSubject<string>('');

  private inputDataTypeSearchTerms: BehaviorSubject<string> =
    new BehaviorSubject<string>('');

  private destroy = new Subject<void>();

  searchTermValue = '';

  challenges: Challenge[] = [];
  totalChallengesCount = 0;

  @ViewChild('calendar') calendar?: Calendar;
  customMonthRange!: Date[] | undefined;
  isCustomYear = false;

  selectedYear!: DateRange | string | undefined;
  selectedMinStartDate!: string | undefined;
  selectedMaxStartDate!: string | undefined;

  pageNumber = 0;
  pageSize = 24;
  searchResultsCount = 0;

  // define filters
  startYearRangeFilter: Filter = challengeStartYearRangeFilter;

  // checkboxFilters: Filter[] = [
  //   challengeStatusFilter,
  //   // challengeDifficultyFilter,
  //   challengeSubmissionTypesFilter,
  //   challengeIncentiveTypesFilter,
  // ];
  statusFilter = challengeStatusFilter;
  submissionTypesFilter = challengeSubmissionTypesFilter;
  incentivesFilter = challengeIncentivesFilter;

  platformsFilter = challengePlatformsFilter;
  inputDataTypesFilter = challengeInputDataTypesFilter;
  organizationsFilter = challengeOrganizationsFilter;
  // dropdownFilters: Filter[] = [
  //   challengePlatformFilter,
  //   challengeInputDataTypeFilter,
  //   challengeOrganizationFilter,
  //   challengeOrganizaterFilter,
  // ];

  selectedStatus: string[] = [];
  selectedSubmissionTypes: string[] = [];
  selectedIncentives: string[] = [];
  selectedPlatforms: string[] = [];

  selectedOrgs: number[] = [];
  selectedInputDataTypes: string[] = [];

  sortFilters: FilterValue[] = challengeSortFilterValues;
  sortedBy!: string;

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
    // set default selection
    this.sortedBy = challengeSortFilterValues[0].value as string;

    this.activatedRoute.queryParams.subscribe((params) => {
      if (params['minStartDate'] || params['maxStartDate']) {
        this.selectedYear = 'custom';
        this.isCustomYear = true;

        this.selectedMinStartDate = params['minStartDate'];
        this.selectedMaxStartDate = params['maxStartDate'];

        const yearRange = [
          params['minStartDate'] ? new Date(params['minStartDate']) : undefined,
          params['maxStartDate'] ? new Date(params['maxStartDate']) : undefined,
        ];

        this.customMonthRange = yearRange as Date[];
      }

      if (params['status']) {
        const status = Array.isArray(params['status'])
          ? params['status']
          : [params['status']];
        this.selectedStatus = status;
      }

      if (params['submissionTypes']) {
        const submissionTypes = Array.isArray(params['submissionTypes'])
          ? params['submissionTypes']
          : [params['submissionTypes']];
        this.selectedSubmissionTypes = submissionTypes;
      }

      if (params['incentives']) {
        const incentives = Array.isArray(params['incentives'])
          ? params['incentives']
          : [params['incentives']];
        this.selectedIncentives = incentives;
      }

      if (params['platforms']) {
        const platforms = Array.isArray(params['platforms'])
          ? params['platforms']
          : [params['platforms']];
        this.selectedPlatforms = platforms;
      }

      if (params['inputDataTypes']) {
        const inputDataTypes = Array.isArray(params['inputDataTypes'])
          ? params['inputDataTypes']
          : [params['inputDataTypes']];
        this.selectedInputDataTypes = inputDataTypes;
      }

      if (params['organizations']) {
        const organizations = Array.isArray(params['organizations'])
          ? params['organizations']
          : [params['organizations']];
        this.selectedOrgs = organizations;
      }

      if (params['pageNumber']) {
        this.pageNumber = params['pageNumber'];
      }

      if (params['pageSize']) {
        this.pageSize = params['pageSize'];
      }

      if (params['sort']) {
        this.sortedBy = params['sort'];
      }

      const defaultQuery = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortedBy,
        // searchTerms: params['searchTerms'] || undefined,
        minStartDate: this.selectedMinStartDate,
        maxStartDate: this.selectedMaxStartDate,
        status: this.selectedStatus,
        submissionTypes: this.selectedSubmissionTypes,
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
    this.challengePlatformService.listChallengePlatforms().subscribe(
      (page) =>
        (this.platformsFilter.values = page.challengePlatforms.map(
          (platform) => ({
            value: platform.slug,
            label: platform.name,
            active: false,
          })
        ))
    );

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

    // const defaultQuery: ChallengeSearchQuery = {
    //   pageNumber: this.pageNumber,
    //   pageSize: this.pageSize,
    //   searchTerms: this.searchTermValue,
    //   sort: this.sortedBy,
    //   minStartDate: this.selectedYear?.start || undefined,
    //   maxStartDate: this.selectedYear?.end || undefined,
    // } as ChallengeSearchQuery;
  }

  ngAfterContentInit(): void {
    // updating year checkbox not working for some reasons
    // this.selectedYear = {
    //   start: urlQuery.minStartDate as string,
    //   end: urlQuery.maxStartDate as string,
    // } as DateRange;
    // this.sortedBy = urlQuery.sort as string;

    this.searchTerms
      .pipe(
        skip(1),
        debounceTime(400),
        distinctUntilChanged(),
        takeUntil(this.destroy)
      )
      .subscribe((searchTerms) => {
        // update query string in url
        this.router.navigate([], { queryParams: { searchTerms } });
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

  onSearchChange(): void {
    // update searchTerms to trigger the query' searchTerm
    this.searchTerms.next(this.searchTermValue);
  }

  onYearChange(): void {
    this.isCustomYear = (this.selectedYear as string) === 'custom';
    if (!this.isCustomYear) {
      // update query with new year range
      // const newQuery = assign(this.query.getValue(), {
      //   minStartDate: yearRange ? yearRange.start : undefined,
      //   maxStartDate: yearRange ? yearRange.end : undefined,
      // });
      // this.query.next(newQuery);
      this.router.navigate([], {
        queryParamsHandling: 'merge',
        queryParams: {
          minStartDate: this.selectedMinStartDate,
          maxStartDate: this.selectedMaxStartDate,
        },
      });
    }
  }

  onCalendarChange(): void {
    this.isCustomYear = true;
    if (this.calendar) {
      // const newQuery = assign(this.query.getValue(), {
      //   minStartDate: this.datePipe.transform(
      //     this.calendar.value[0],
      //     'yyyy-MM-dd'
      //   ),
      //   maxStartDate: this.datePipe.transform(
      //     this.calendar.value[1],
      //     'yyyy-MM-dd'
      //   ),
      // });
      // this.query.next(newQuery);
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
        status: selected,
      },
    });
  }

  onSubmissionTypesChange(selected: string[]): void {
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        submissionTypes: selected,
      },
    });
  }

  onIncentivesChange(selected: string[]): void {
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        incentives: selected,
      },
    });
  }

  onPlatformsChange(selected: string[]): void {
    this.router.navigate([], {
      queryParams: {
        platforms: selected,
      },
    });
  }

  // onCheckboxSelectionChange(selected: string[], queryName: string): void {
  //   // const newQuery = assign(this.query.getValue(), {
  //   //   [queryName]: selected,
  //   // });
  //   // this.query.next(newQuery);
  //   this.router.navigate([], {
  //     queryParams: {
  //       [queryName]: selected,
  //     },
  //   });
  // }

  // onPlatformsChange(selected: string[]): void {
  //   this.router.navigate([], {
  //     queryParams: {
  //       platforms: selected,
  //     },
  //   });
  // }

  // onPlatformSearchChange(searched: string): void {
  //   this.orgSearchTerms.next(searched);
  // }

  onInputDataTypesChange(selected: string[]): void {
    this.router.navigate([], {
      queryParams: {
        inputDataTypes: selected,
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
        organizations: selected,
      },
    });
  }

  onOrganizationSearchChange(searched: string): void {
    this.organizationSearchTerms.next(searched);
  }

  // onDropdownSelectionChange(
  //   selected: string[] | number[],
  //   queryName: string
  // ): void {
  //   if (queryName === 'inputDataTypes') {
  //     this.selectedOrgs = this.inputDataTypesFilter.values.filter((value) =>
  //       (selected as string[]).includes(value.value as string)
  //     );
  //   }

  //   if (queryName === 'organizations') {
  //     this.selectedOrgs = this.organizationsFilter.values.filter((value) =>
  //       (selected as number[]).includes(value.value as number)
  //     );
  //   }

  //   this.router.navigate([], {
  //     queryParams: {
  //       [queryName]: selected,
  //     },
  //   });
  // const newQuery = assign(this.query.getValue(), {
  //   [queryName]: selected,
  // });
  // this.query.next(newQuery);
  // }

  // onDropdownSearchChange(searched: string): void {
  //   this.orgSearchTerms.next(searched);
  // }

  onSortChange(): void {
    // const newQuery = assign(this.query.getValue(), {
    //   sort: this.sortedBy,
    // });
    // this.query.next(newQuery);
    this.router.navigate([], {
      queryParamsHandling: 'merge',
      queryParams: {
        sort: this.sortedBy,
      },
    });
  }

  onPageChange(event: any) {
    // const newQuery = assign(this.query.getValue(), {
    //   pageNumber: event.page,
    //   pageSize: event.rows,
    // });
    // this.query.next(newQuery);
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
