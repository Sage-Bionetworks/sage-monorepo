import { Component, OnInit } from '@angular/core';
import {
  Challenge,
  DateRange,
} from '@sagebionetworks/api-client-angular-deprecated';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';
import { challengeStartYearRangeFilterValues } from './challenge-search-filters-values';
import { FilterValue } from './filter-value.model';
import { MOCK_CHALLENGES } from '@sagebionetworks/challenge-registry/ui';
import { BehaviorSubject, of, switchMap, tap } from 'rxjs';
import { ChallengeSearchQuery } from './challenge-search-query';
import { assign } from 'lodash';

@Component({
  selector: 'challenge-registry-challenge-search',
  templateUrl: './challenge-search.component.html',
  styleUrls: ['./challenge-search.component.scss'],
})
export class ChallengeSearchComponent implements OnInit {
  public appVersion: string;

  private query: BehaviorSubject<ChallengeSearchQuery> =
    new BehaviorSubject<ChallengeSearchQuery>({
      limit: 0,
      offset: 0,
      startYearRange: {} as DateRange,
    });

  challenges: Challenge[] = [];
  totalChallengesCount!: number;

  customYear!: Date;
  isCustomYear = false;
  selectedYear!: DateRange | string | undefined;

  limit = 10;
  offset = 0;
  searchResultsCount = 0;

  challengeStartYearRangeFilterValues: FilterValue[] =
    challengeStartYearRangeFilterValues;

  constructor(private readonly configService: ConfigService) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit() {
    this.selectedYear = this.challengeStartYearRangeFilterValues[0].value;
    this.totalChallengesCount = MOCK_CHALLENGES.length;
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
          // mock up fitered query challenges
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
        this.searchResultsCount = page.length;
        this.challenges = page;
      });
  }

  onYearChange(event: any) {
    this.isCustomYear = event.value === 'custom';
    const newQuery = assign(this.query.getValue(), {
      startYearRange: event.value,
    });
    this.query.next(newQuery);
  }
}
