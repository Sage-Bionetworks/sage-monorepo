import { Injectable } from '@angular/core';
import { Challenge, ChallengeService } from '@challenge-registry/api-angular';
import { BehaviorSubject, Observable } from 'rxjs';
import { filter, switchMap, tap } from 'rxjs/operators';
import { isNot } from 'type-guards';

@Injectable({
  providedIn: 'root',
})
export class ChallengeDataService {
  private challenge: BehaviorSubject<Challenge | undefined> =
    new BehaviorSubject<Challenge | undefined>(undefined);

  constructor(private challengeService: ChallengeService) {}

  fetchChallenge(owner: string, name: string): Observable<Challenge> {
    return this.challengeService.getChallenge(owner, name).pipe(
      tap((challenge) => this.challenge.next(challenge)),
      switchMap(() => this.challenge.asObservable()),
      filter(isNot(undefined))
    );
  }

  setChallenge(challenge: Challenge | undefined): void {
    this.challenge.next(challenge);
  }

  getChallenge(): Observable<Challenge | undefined> {
    return this.challenge.asObservable();
    // .pipe(filter((challenge) => challenge !== undefined));
  }
}
