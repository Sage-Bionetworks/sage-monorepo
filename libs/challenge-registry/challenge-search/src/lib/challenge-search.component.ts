import { AfterContentInit, Component, OnInit, ViewChild } from '@angular/core';
import {
  Challenge,
  ChallengePlatform,
  DateRange,
} from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';
import { Organization } from '@sagebionetworks/challenge-registry/api-client-angular';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';
import {
  Filter,
  FilterValue,
  MOCK_CHALLENGES,
  MOCK_ORGANIZATIONS,
} from '@sagebionetworks/challenge-registry/ui';
import { MOCK_PLATFORMS } from './mock-platforms';
import {
  challengeStartYearRangeFilter,
  challengeStatusFilter,
  challengeDifficultyFilter,
  challengeSubmissionTypesFilter,
  challengeInputDataTypeFilter,
  challengeIncentiveTypesFilter,
  challengePlatformFilter,
  challengeOrganizationFilter,
} from './challenge-search-filters';
import { challengeSortFilterValues } from './challenge-search-filters-values';
import { BehaviorSubject, Observable, of, switchMap, tap } from 'rxjs';
import { ChallengeSearchQuery } from './challenge-search-query';
import { Calendar } from 'primeng/calendar';
import { DatePipe } from '@angular/common';
import { assign } from 'lodash';
import { isNotNullOrUndefined } from 'type-guards';

@Component({
  selector: 'challenge-registry-challenge-search',
  templateUrl: './challenge-search.component.html',
  styleUrls: ['./challenge-search.component.scss'],
})
export class ChallengeSearchComponent implements OnInit, AfterContentInit {
  public appVersion: string;
  datepipe: DatePipe = new DatePipe('en-US');

  private query: BehaviorSubject<ChallengeSearchQuery> =
    new BehaviorSubject<ChallengeSearchQuery>({
      limit: 0,
      offset: 0,
      sort: undefined,
      startYearRange: {},
      status: [],
      inputDataTypes: [],
      difficulty: [],
      submissionTypes: [],
      incentiveTypes: [],
      platforms: [],
      organizations: [],
    });

  challenges: Challenge[] = [];
  totalChallengesCount!: number;

  @ViewChild('calendar') calendar?: Calendar;
  customMonthRange!: DateRange;
  isCustomYear = false;
  selectedYear!: DateRange | string | undefined;

  limit = 10;
  offset = 0;
  searchResultsCount = 0;

  // define filters
  startYearRangeFilter: Filter = challengeStartYearRangeFilter;

  checkboxFilters: Filter[] = [
    challengeStatusFilter,
    challengeDifficultyFilter,
    challengeInputDataTypeFilter,
    challengeSubmissionTypesFilter,
    challengeIncentiveTypesFilter,
    challengePlatformFilter,
  ];

  dropdownFilters: Filter[] = [challengeOrganizationFilter];

  sortFilters: FilterValue[] = challengeSortFilterValues;
  sortedBy!: string;

  constructor(private readonly configService: ConfigService) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit() {
    this.selectedYear = this.startYearRangeFilter.values[0].value;
    this.totalChallengesCount = MOCK_CHALLENGES.length;

    // mock up service to query all unique input data types
    this.listInputDataTypes().subscribe(
      (dataTypes) =>
        // update input data types filter values
        (this.checkboxFilters[2].values = dataTypes.map((dataType) => ({
          value: dataType,
          label: this.titleCase(dataType, '-'),
          active: false,
        })))
    );
    // mock up service to query all unique platforms
    this.listPlatforms().subscribe(
      (platforms) =>
        // update input data types filter values
        (this.checkboxFilters[5].values = platforms.map((platform) => ({
          value: platform.id,
          label: platform.displayName,
          active: false,
        })))
    );
    // mock up service to query all unique organizations
    this.listOrganizations().subscribe(
      (organizations) =>
        // update input data types filter values
        (this.dropdownFilters[0].values = organizations.map((org) => ({
          value: org.login,
          label: org.name,
          avatarUrl: org.avatarUrl,
          active: false,
        })))
    );

    // triger initial query
    const defaultQuery = {
      startYearRange: this.selectedYear,
      sort: this.sortFilters[0].value,
      ...this.query,
    } as ChallengeSearchQuery;
    this.query.next(defaultQuery);
  }

  ngAfterContentInit(): void {
    this.query
      .pipe(
        tap((query) => console.log('List challenges', query)),
        switchMap((query) => {
          // mock up challengeList service with defined query
          const res = MOCK_CHALLENGES.filter((c) => {
            return (
              c.startDate &&
              query.startYearRange?.start &&
              query.startYearRange?.end &&
              new Date(c.startDate) >= new Date(query.startYearRange.start) &&
              new Date(c.startDate) <= new Date(query.startYearRange.end) &&
              this.checkOverlapped(c.status, query.status) &&
              this.checkOverlapped(c.difficulty, query.difficulty) &&
              this.checkOverlapped(c.inputDataTypes, query.inputDataTypes) &&
              this.checkOverlapped(c.submissionTypes, query.submissionTypes) &&
              this.checkOverlapped(c.incentiveTypes, query.incentiveTypes) &&
              this.checkOverlapped(c.platformId, query.platforms) &&
              this.checkOverlapped(c.organizations, query.organizations)
            );
          });
          return of(this.sortChallenges(res, query.sort));
        })
      )
      .subscribe((page) => {
        // update challenges and total number of results
        this.searchResultsCount = page.length;
        this.challenges = page;
      });
  }

  onYearChange(event: any): void {
    this.isCustomYear = event.value === 'custom';
    const yearRange = event.value;
    // update query with new year range
    const newQuery = assign(this.query.getValue(), {
      startYearRange: yearRange,
    });
    this.query.next(newQuery);
  }

  onCalendarChange(): void {
    this.isCustomYear = true;
    if (this.calendar) {
      const yearRange = {
        start: this.datepipe.transform(this.calendar.value[0], 'yyyy-MM-dd'),
        end: this.datepipe.transform(this.calendar.value[1], 'yyyy-MM-dd'),
      } as DateRange;

      const newQuery = assign(this.query.getValue(), {
        startYearRange: yearRange,
      });
      this.query.next(newQuery);
    }
  }

  onCheckboxChange(selected: string[], queryName: string): void {
    const newQuery = assign(this.query.getValue(), {
      [queryName]: selected,
    });
    this.query.next(newQuery);
  }

  onDropdownChange(selected: string[], queryName: string): void {
    const newQuery = assign(this.query.getValue(), {
      [queryName]: selected,
    });
    this.query.next(newQuery);
  }

  onSortChange(event: any): void {
    const newQuery = assign(this.query.getValue(), {
      sort: event.value,
    });
    this.query.next(newQuery);
  }

  titleCase(string: string, split: string): string {
    // tranform one word to title-case word
    return string
      .split(split)
      .map((text) => text[0].toUpperCase() + text.slice(1).toLowerCase())
      .join(' ');
  }

  private listInputDataTypes(): Observable<string[]> {
    // update input data type values - API requires
    const allTypes = [...new Set(MOCK_CHALLENGES.map((c) => c.inputDataTypes))];
    const uniqueTypes = [
      ...new Set(
        allTypes.filter(isNotNullOrUndefined).reduce((o, c) => o.concat(c), [])
      ),
    ];
    const sortTypes = uniqueTypes.includes('other')
      ? [...uniqueTypes.filter((x) => x !== 'other').sort(), 'other']
      : uniqueTypes.sort();

    return of(sortTypes);
  }

  private listPlatforms(): Observable<ChallengePlatform[]> {
    return of(MOCK_PLATFORMS);
  }

  private listOrganizations(): Observable<Organization[]> {
    return of(MOCK_ORGANIZATIONS);
  }

  // tmp - Removed once Service is used
  checkOverlapped(property: any, filterValues: any): boolean {
    if (filterValues && filterValues.length > 0) {
      // filter applied, but no property presents
      if (!property) return false;
      // check overlap between two arrays, otherwise return true
      const propertyValues = property instanceof Array ? property : [property];
      return propertyValues.some((value: any) => filterValues?.includes(value));
    } else {
      return true; // no filter applied
    }
  }

  // mock up sorting challenges by certain property
  private sortChallenges(
    challenges: Challenge[],
    sortBy: keyof Challenge | undefined
  ): Challenge[] {
    if (!sortBy) return challenges;

    if (['startDate', 'endDate'].includes(sortBy as string)) {
      // if it's starting soon, the status should be 'upcoming',
      // otherwise, it's closing soon and status should be 'active',
      const status = sortBy === 'startDate' ? 'upcoming' : 'active';
      // sort challenges by startDate
      // the sooner the challenge is going to start/end, the closer to the 1st card
      // note: since it's a mock up func, the undefined of startDate/endDate is not considered here
      return challenges
        .filter((c) => c.status === status)
        .sort(
          (a, b) =>
            +new Date(b.startDate as string) - +new Date(a.startDate as string)
        );
    } else {
      return challenges.sort(
        (a, b) => (b[sortBy] as number) - (a[sortBy] as number)
      );
    }
  }
}
