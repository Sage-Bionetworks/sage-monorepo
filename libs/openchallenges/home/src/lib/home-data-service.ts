import { Injectable } from '@angular/core';
import {
  ChallengeAnalyticsService,
  ChallengesPerYear,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable, filter, of } from 'rxjs';
import { isNotNull } from 'type-guards';

@Injectable({
  providedIn: 'root',
})
export class HomeDataService {
  challengesPerYear$: Observable<ChallengesPerYear | null> = of(null);
  constructor(private challengeAnalyticsService: ChallengeAnalyticsService) {}

  setChallengesPerYear() {
    this.challengesPerYear$ = this.challengeAnalyticsService.getChallengesPerYear();
  }

  getChallengesPerYear(): Observable<ChallengesPerYear> {
    return this.challengesPerYear$.pipe(filter(isNotNull));
  }
}
