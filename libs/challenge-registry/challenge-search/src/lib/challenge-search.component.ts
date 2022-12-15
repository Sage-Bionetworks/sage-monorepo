import { Component, OnInit, ViewChild } from '@angular/core';
import {
  Challenge,
  DateRange,
} from '@sagebionetworks/api-client-angular-deprecated';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';
import {
  challengeStartYearRangeFilterValues,
  challengeStatusFilterValues,
  challengeDifficultyFilterValues,
  challengeSubmissionTypesFilterValues,
  challengeInputDataTypeFilterValues,
  challengeIncentiveTypesFilterValues,
} from './challenge-search-filters-values';
import {
  FilterValue,
  MOCK_CHALLENGES,
} from '@sagebionetworks/challenge-registry/ui';
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
export class ChallengeSearchComponent implements OnInit {
  public appVersion: string;
  datepipe: DatePipe = new DatePipe('en-US');

  private query: BehaviorSubject<ChallengeSearchQuery> =
    new BehaviorSubject<ChallengeSearchQuery>({
      limit: 0,
      offset: 0,
      startYearRange: {} as DateRange,
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

  startYearRangeValues: FilterValue[] = challengeStartYearRangeFilterValues;
  checkboxfilters: { label: string; values: FilterValue[] }[] = [
    {
      label: 'Status',
      values: challengeStatusFilterValues,
    },
    {
      label: 'Difficulty',
      values: challengeDifficultyFilterValues,
    },
    {
      label: 'Submission Type',
      values: challengeSubmissionTypesFilterValues,
    },
    {
      label: 'Input Data Type',
      values: challengeInputDataTypeFilterValues,
    },
    {
      label: 'Platform',
      values: challengeIncentiveTypesFilterValues,
    },
  ];

  constructor(private readonly configService: ConfigService) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit() {
    this.selectedYear = this.startYearRangeValues[0].value;
    this.totalChallengesCount = MOCK_CHALLENGES.length;

    this.listInputDataType().subscribe(
      (dataTypes) =>
        // update input data types filter values
        (this.checkboxfilters[3].values = dataTypes.map((dataType) => ({
          value: dataType,
          label: this.titleCase(dataType, '-'),
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
              new Date(c.startDate) <= new Date(query.startYearRange.end)
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

  titleCase(string: string, split: string): string {
    // tranform one word to title-case word
    return string
      .split(split)
      .map((text) => text[0].toUpperCase() + text.slice(1).toLowerCase())
      .join(' ');
  }

  listInputDataType(): Observable<string[]> {
    // update input data type values - API requires
    const allTypes = [...new Set(MOCK_CHALLENGES.map((c) => c.inputDataTypes))];
    const uniqueTypes = [
      ...new Set(
        allTypes.filter(isNotNullOrUndefined).reduce((o, c) => o.concat(c), [])
      ),
    ].sort();
    return of(uniqueTypes);
  }
}
