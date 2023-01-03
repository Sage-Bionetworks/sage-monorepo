import { AfterContentInit, Component, OnInit, ViewChild } from '@angular/core';
import {
  Challenge,
  ChallengePlatform,
  DateRange,
} from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';
import {
  Filter,
  MOCK_CHALLENGES,
  MOCK_ORGANIZATIONS,
} from '@sagebionetworks/challenge-registry/ui';
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
import { BehaviorSubject, Observable, of, switchMap, tap } from 'rxjs';
import { ChallengeSearchQuery } from './challenge-search-query';
import { Calendar } from 'primeng/calendar';
import { DatePipe } from '@angular/common';
import { assign } from 'lodash';
import { isNotNullOrUndefined } from 'type-guards';
import { MOCK_PLATFORMS } from './mock-platforms';
import { Organization } from '@sagebionetworks/challenge-registry/api-client-angular';

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

  checkboxfilters: Filter[] = [
    challengeStatusFilter,
    challengeDifficultyFilter,
    challengeInputDataTypeFilter,
    challengeSubmissionTypesFilter,
    challengeIncentiveTypesFilter,
    challengePlatformFilter,
  ];

  dropdownfilters: Filter[] = [challengeOrganizationFilter];

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
        (this.checkboxfilters[2].values = dataTypes.map((dataType) => ({
          value: dataType,
          label: this.titleCase(dataType, '-'),
          active: false,
        })))
    );
    // mock up service to query all unique platforms
    this.listPlatforms().subscribe(
      (platforms) =>
        // update input data types filter values
        (this.checkboxfilters[5].values = platforms.map((platform) => ({
          value: platform.id,
          label: platform.displayName,
          active: false,
        })))
    );
    // mock up service to query all unique organizations
    this.listOrganizations().subscribe(
      (organizations) =>
        // update input data types filter values
        (this.dropdownfilters[0].values = organizations.map((org) => ({
          value: org.login,
          label: org.name,
          active: false,
        })))
    );

    // triger initial query
    const defaultQuery = {
      startYearRange: this.selectedYear,
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
          return of(res);
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
    // if property(challenge property) presents and filter (filterValues) applied,
    // check overlap between two arrays, otherwise return true
    if (property && filterValues && filterValues.length > 0) {
      // x = isinstance(x, list) ? x : [x];
      return [property].some((value: any) => filterValues?.includes(value));
    } else {
      return true;
    }
  }
}
