import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'openchallenges-challenge-stats',
  templateUrl: './challenge-stats.component.html',
  styleUrls: ['./challenge-stats.component.scss'],
})
export class ChallengeStatsComponent implements OnInit {
  @Input() loggedIn = false;
  mockViews!: number;
  mockStargazers!: number;

  ngOnInit(): void {
    this.mockViews = 2;
    this.mockStargazers = 9_999;
  }

  shorthand(n: number) {
    return Intl.NumberFormat('en-US', {
      notation: 'compact',
      maximumFractionDigits: 1,
    }).format(n);
  }
}
