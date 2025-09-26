import { Component, Input, OnInit } from '@angular/core';

import { MatIconModule } from '@angular/material/icon';
import { Challenge } from '@sagebionetworks/openchallenges/api-client';

@Component({
  selector: 'openchallenges-challenge-stats',
  imports: [MatIconModule],
  templateUrl: './challenge-stats.component.html',
  styleUrls: ['./challenge-stats.component.scss'],
})
export class ChallengeStatsComponent implements OnInit {
  @Input({ required: true }) challenge: Challenge | undefined;
  @Input({ required: true }) loggedIn = false;

  mockViews!: number;
  mockStargazers!: number;

  ngOnInit(): void {
    this.mockViews = 5_000;
    this.mockStargazers = 2;
  }

  shorthand(n: number | undefined) {
    if (n) {
      return Intl.NumberFormat('en-US', {
        maximumFractionDigits: 1,
      }).format(n);
    } else {
      return 0;
    }
  }
}
