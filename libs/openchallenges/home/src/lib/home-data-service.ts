import { Injectable } from '@angular/core';
import {
  ChallengeAnalyticsService,
  ChallengesPerYear,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { BehaviorSubject, Observable, filter, map } from 'rxjs';
import { isNotNull } from 'type-guards';

@Injectable({
  providedIn: 'root',
})
export class HomeDataService {
  private challengesPerYear = new BehaviorSubject<ChallengesPerYear | null>(
    null
  );

  constructor(private challengeAnalyticsService: ChallengeAnalyticsService) {}

  setChallengesPerYear() {
    this.challengeAnalyticsService
      .getChallengesPerYear()
      .pipe(map((res) => this.challengesPerYear.next(res)));
  }

  getChallengesPerYear(): Observable<ChallengesPerYear> {
    return this.challengesPerYear.pipe(filter(isNotNull));
  }
}
