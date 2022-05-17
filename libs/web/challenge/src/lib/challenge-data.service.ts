import { Injectable } from '@angular/core';
import { Challenge } from '@challenge-registry/api-angular';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ChallengeDataService {
  private challenge: BehaviorSubject<Challenge | undefined> =
    new BehaviorSubject<Challenge | undefined>(undefined);

  setChallenge(challenge: Challenge | undefined): void {
    this.challenge.next(challenge);
  }

  getChallenge(): Observable<Challenge | undefined> {
    return this.challenge.asObservable();
    // .pipe(filter((challenge) => challenge !== undefined));
  }
}
