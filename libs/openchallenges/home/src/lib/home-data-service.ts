import { Injectable } from '@angular/core';
import {
  Challenge,
  ChallengeService,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Observable, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class HomeDataService {
  challenges$!: Observable<Challenge[]>;

  constructor(private challengeService: ChallengeService) {}

  fetchAllChallenges() {
    this.challenges$ = this.challengeService
      .listChallenges({ pageSize: 1000 })
      .pipe(map((page) => page.challenges));
  }

  getAllChallenges(): Observable<Challenge[]> {
    return this.challenges$;
  }
}
