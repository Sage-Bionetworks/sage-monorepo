import { Component, OnInit } from '@angular/core';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';
import { challengeStartYearRangeFilterValues } from './challenge-search-filters-values';
import { FilterValue } from './filter-value.model';

@Component({
  selector: 'challenge-registry-challenge-search',
  templateUrl: './challenge-search.component.html',
  styleUrls: ['./challenge-search.component.scss'],
})
export class ChallengeSearchComponent implements OnInit {
  public appVersion: string;
  // yearSelect = 'all';
  // panelOpenState = true;

  customYear!: Date;
  yearSelections!: string[];
  selectedYear!: string | undefined;
  challengeStartYearRangeFilterValues: FilterValue[] =
    challengeStartYearRangeFilterValues;

  constructor(private readonly configService: ConfigService) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit() {
    this.selectedYear = this.challengeStartYearRangeFilterValues[1].label;
  }
}
