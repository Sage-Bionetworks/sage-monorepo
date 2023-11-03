import { Component, Input, OnInit } from '@angular/core';
import { Challenge } from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'openchallenges-challenge-stats',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './challenge-stats.component.html',
  styleUrls: ['./challenge-stats.component.scss'],
})
export class ChallengeStatsComponent implements OnInit {
  @Input({ required: true }) loggedIn = false;
  challenge$!: Observable<Challenge>;
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
