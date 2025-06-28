import { inject, Injectable } from '@angular/core';
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
  private readonly challengeAnalyticsService = inject(ChallengeAnalyticsService);

  challengesPerYear$: Observable<ChallengesPerYear | null> = of(null);

  setChallengesPerYear() {
    this.challengesPerYear$ = this.challengeAnalyticsService.getChallengesPerYear();
  }

  getChallengesPerYear(): Observable<ChallengesPerYear> {
    return this.challengesPerYear$.pipe(filter(isNotNull));
  }
}
