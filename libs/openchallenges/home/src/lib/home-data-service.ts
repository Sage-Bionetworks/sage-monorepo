import { Injectable } from '@angular/core';
import {
  ChallengeAnalyticsService,
  ChallengesPerYear,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class HomeDataService {
  challengesPerYear$!: Observable<ChallengesPerYear>;

  constructor(private challengeAnalyticsService: ChallengeAnalyticsService) {}

  fetchChallengesPerYear() {
    this.challengesPerYear$ = this.challengeAnalyticsService
      .getChallengesPerYear()
      .pipe(map((page) => page));
  }

  getChallengesPerYear(): Observable<ChallengesPerYear> {
    return this.challengesPerYear$;
  }
}
