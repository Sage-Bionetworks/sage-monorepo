import { Component, OnInit } from '@angular/core';
import { Challenge } from '@sagebionetworks/api-client-angular-deprecated';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';
import { challengeStartYearRangeFilterValues } from './challenge-search-filters-values';
import { FilterValue } from './filter-value.model';
import { MOCK_CHALLENGES } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-challenge-search',
  templateUrl: './challenge-search.component.html',
  styleUrls: ['./challenge-search.component.scss'],
})
export class ChallengeSearchComponent implements OnInit {
  public appVersion: string;
  // yearSelect = 'all';
  // panelOpenState = true;
  challenges: Challenge[] = [];
  searchResultsCount = 0;
  customYear!: Date;
  isCustomYear = false;
  selectedYear!: { start?: Date; end?: Date } | string | undefined;
  challengeStartYearRangeFilterValues: FilterValue[] =
    challengeStartYearRangeFilterValues;

  constructor(private readonly configService: ConfigService) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit() {
    this.selectedYear = this.challengeStartYearRangeFilterValues[0].value;
    this.challenges = MOCK_CHALLENGES;
    this.searchResultsCount = MOCK_CHALLENGES.length;
  }

  onRadioChange(event: any) {
    this.isCustomYear = event.value === 'custom';
  }
}
