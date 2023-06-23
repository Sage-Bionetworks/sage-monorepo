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
  challengeInputDataTypeFilter,
  // challengeIncentiveTypesFilter,
  challengePlatformFilter,
  challengeOrganizationFilter,
  challengeOrganizaterFilter,
  challengeIncentiveTypesFilter,
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
  datepipe: DatePipe = new DatePipe('en-US');

  private query: BehaviorSubject<ChallengeSearchQuery> =
    new BehaviorSubject<ChallengeSearchQuery>({});

  // set a default behaviorSubject to trigger searchTearm's changes
  private searchTerms: BehaviorSubject<string> = new BehaviorSubject<string>(
    ''
  );

  private orgSearchTerms: BehaviorSubject<string> = new BehaviorSubject<string>(
    ''
  );

  private inputDataTypeSearchTerms: BehaviorSubject<string> =
    new BehaviorSubject<string>('');

  private destroy = new Subject<void>();
  searchTermValue = '';

  challenges: Challenge[] = [];
  totalChallengesCount = 0;

  @ViewChild('calendar') calendar?: Calendar;
  customMonthRange!: DateRange;
  isCustomYear = false;
  selectedYear!: DateRange | undefined;

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
  incentiveTypesFilter = challengeIncentiveTypesFilter;

  dropdownFilters: Filter[] = [
    challengePlatformFilter,
    challengeInputDataTypeFilter,
    challengeOrganizationFilter,
    challengeOrganizaterFilter,
  ];

  selectedStatus: string[] = [];
  selectedSubmissionTypes: string[] = [];
  selectedIncentiveTypes: string[] = [];

  selectedOrgs: FilterValue[] = [];
  selectedInputDataTypes: FilterValue[] = [];

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
    this.selectedYear = this.startYearRangeFilter.values[0].value as DateRange;
    this.sortedBy = challengeSortFilterValues[0].value as string;

    // update the total number of challenges in database with empty query
    this.challengeService
      .listChallenges({} as ChallengeSearchQuery)
      .subscribe((page) => (this.totalChallengesCount = page.totalElements));

    // update platform filter values
    this.challengePlatformService.listChallengePlatforms().subscribe(
      (page) =>
        (challengePlatformFilter.values = page.challengePlatforms.map(
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

        challengeInputDataTypeFilter.values = union(
          searchedInputDataTypes,
          this.selectedInputDataTypes
        );
      });

    // update organization filter values
    this.orgSearchTerms
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

        challengeOrganizationFilter.values = union(
          searchedOrgs,
          this.selectedOrgs
        );
      });

    // // mock up service to query all unique organizers
    // this.listOrganizers().subscribe(
    //   (organizers) =>
    //     (this.dropdownFilters[1].values = organizers.map((organizer) => ({
    //       value: organizer.challengeId,
    //       label: organizer.name,
    //       avatarUrl: organizer.avatarUrl,
    //       active: false,
    //     })))
    // );

    // const defaultQuery: ChallengeSearchQuery = {
    //   pageNumber: this.pageNumber,
    //   pageSize: this.pageSize,
    //   searchTerms: this.searchTermValue,
    //   sort: this.sortedBy,
    //   minStartDate: this.selectedYear?.start || undefined,
    //   maxStartDate: this.selectedYear?.end || undefined,
    // } as ChallengeSearchQuery;

    this.activatedRoute.queryParams.subscribe((params) => {
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

      if (params['incentiveTypes']) {
        const incentiveTypes = Array.isArray(params['incentiveTypes'])
          ? params['incentiveTypes']
          : [params['incentiveTypes']];
        this.selectedIncentiveTypes = incentiveTypes;
      }

      const defaultQuery = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize,
        sort: this.sortedBy,
        searchTerms: params['searchTerms'] || undefined,
        minStartDate: params['minStartDate'] || undefined,
        maxStartDate: params['maxStartDate'] || undefined,
        status: this.selectedStatus,
        submissionTypes: params['submissionTypes'] || undefined,
        incentives: params['incentives'] || undefined,
        inputDataTypes: params['inputDataTypes'] || undefined,
        organizations: params['organizations'] || undefined,
      } as ChallengeSearchQuery;

      this.query.next(defaultQuery);

      // updating year checkbox not working for some reasons
      // this.selectedYear = {
      //   start: urlQuery.minStartDate as string,
      //   end: urlQuery.maxStartDate as string,
      // } as DateRange;
      // this.sortedBy = urlQuery.sort as string;
      // this.selectedStatus = [urlQuery.status] as string[];
      console.log(this.selectedStatus);
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
    const yearRange = this.selectedYear;
    // update query with new year range
    // const newQuery = assign(this.query.getValue(), {
    //   minStartDate: yearRange ? yearRange.start : undefined,
    //   maxStartDate: yearRange ? yearRange.end : undefined,
    // });
    // this.query.next(newQuery);
    this.router.navigate([], {
      queryParams: {
        minStartDate: yearRange ? yearRange.start : undefined,
        maxStartDate: yearRange ? yearRange.end : undefined,
      },
    });
  }

  onCalendarChange(): void {
    this.isCustomYear = true;
    if (this.calendar) {
      // const newQuery = assign(this.query.getValue(), {
      //   minStartDate: this.datepipe.transform(
      //     this.calendar.value[0],
      //     'yyyy-MM-dd'
      //   ),
      //   maxStartDate: this.datepipe.transform(
      //     this.calendar.value[1],
      //     'yyyy-MM-dd'
      //   ),
      // });
      // this.query.next(newQuery);
      this.router.navigate([], {
        queryParams: {
          minStartDate: this.datepipe.transform(
            this.calendar.value[0],
            'yyyy-MM-dd'
          ),
          maxStartDate: this.datepipe.transform(
            this.calendar.value[1],
            'yyyy-MM-dd'
          ),
        },
      });
    }
  }

  onStatusChange(selected: string[]): void {
    this.router.navigate([], {
      queryParams: {
        status: selected,
      },
    });
  }

  onSubmissionTypesChange(selected: string[]): void {
    this.router.navigate([], {
      queryParams: {
        submissionTypes: selected,
      },
    });
  }

  onIncentiveTypesChange(selected: string[]): void {
    this.router.navigate([], {
      queryParams: {
        incentiveTypes: selected,
      },
    });
  }

  onCheckboxSelectionChange(selected: string[], queryName: string): void {
    // const newQuery = assign(this.query.getValue(), {
    //   [queryName]: selected,
    // });
    // this.query.next(newQuery);
    this.router.navigate([], {
      queryParams: {
        [queryName]: selected,
      },
    });
  }

  onDropdownSelectionChange(
    selected: string[] | number[],
    queryName: string
  ): void {
    if (queryName === 'inputDataTypes') {
      this.selectedOrgs = challengeInputDataTypeFilter.values.filter((value) =>
        (selected as string[]).includes(value.value as string)
      );
    }

    if (queryName === 'organizations') {
      this.selectedOrgs = challengeOrganizationFilter.values.filter((value) =>
        (selected as number[]).includes(value.value as number)
      );
    }

    this.router.navigate([], {
      queryParams: {
        [queryName]: selected,
      },
    });
    // const newQuery = assign(this.query.getValue(), {
    //   [queryName]: selected,
    // });
    // this.query.next(newQuery);
  }

  onDropdownSearchChange(searched: string): void {
    this.orgSearchTerms.next(searched);
  }

  onSortChange(): void {
    // const newQuery = assign(this.query.getValue(), {
    //   sort: this.sortedBy,
    // });
    // this.query.next(newQuery);
    this.router.navigate([], {
      queryParams: {
        sort: this.sortedBy,
      },
    });
  }

  // private listOrganizers(): Observable<ChallengeOrganizer[]> {
  //   return of(MOCK_CHALLENGE_ORGANIZERS);
  // }

  onPageChange(event: any) {
    // const newQuery = assign(this.query.getValue(), {
    //   pageNumber: event.page,
    //   pageSize: event.rows,
    // });
    // this.query.next(newQuery);
    this.router.navigate([], {
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
