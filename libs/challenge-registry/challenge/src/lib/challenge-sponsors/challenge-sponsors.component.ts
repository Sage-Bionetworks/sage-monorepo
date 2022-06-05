import { Component, OnInit } from '@angular/core';
import { ChallengeDataService } from '../challenge-data.service';
import {
  Challenge,
  ChallengeService,
  ChallengeSponsor,
} from '@sagebionetworks/api-angular';
import { filter, switchMap, tap } from 'rxjs';
import { isNotUndefined } from 'type-guards';

@Component({
  selector: 'challenge-registry-challenge-sponsors',
  templateUrl: './challenge-sponsors.component.html',
  styleUrls: ['./challenge-sponsors.component.scss'],
})
export class ChallengeSponsorsComponent implements OnInit {
  challenge!: Challenge;
  login!: string;
  challengeSponsors: ChallengeSponsor[] = [];

  constructor(
    private challengeDataService: ChallengeDataService,
    private challengeService: ChallengeService
  ) {}

  ngOnInit(): void {
    this.challengeDataService
      .getChallenge()
      .pipe(
        filter(isNotUndefined),
        tap((challenge) => (this.challenge = challenge)),
        switchMap(() => this.challengeDataService.getLogin()),
        tap((login) => (this.login = login)),
        switchMap(() =>
          this.challengeService.listChallengeSponsors(
            this.login,
            this.challenge.name
          )
        )
      )
      .subscribe(
        (sponsors) => (this.challengeSponsors = sponsors.challengeSponsors)
      );
  }
}
