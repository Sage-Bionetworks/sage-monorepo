import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'openchallenges-org-profile-stats',
  templateUrl: './org-profile-stats.component.html',
  styleUrls: ['./org-profile-stats.component.scss'],
})
export class OrgProfileStatsComponent implements OnInit {
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
