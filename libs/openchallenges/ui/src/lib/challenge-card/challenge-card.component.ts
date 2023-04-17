import { Component, Input, OnInit } from '@angular/core';
import {
  Challenge,
  ChallengePlatformService,
  SimpleChallengePlatform,
} from '@sagebionetworks/openchallenges/api-client-angular';
import { Challenge as DeprecatedChallenge } from '@sagebionetworks/openchallenges/api-client-angular-deprecated';
// import { startCase } from 'lodash';

@Component({
  selector: 'openchallenges-challenge-card',
  templateUrl: './challenge-card.component.html',
  styleUrls: ['./challenge-card.component.scss'],
})
export class ChallengeCardComponent implements OnInit {
  @Input() challenge!: Challenge;
  // TODO: remove the deprecatedChallenge when real Challenge has all required properties
  @Input() deprecatedChallenge!: DeprecatedChallenge;
  platform!: SimpleChallengePlatform;
  status!: string | undefined;
  incentives!: string;
  statusClass!: string;
  // difficulty!: string | undefined;

  constructor(private challengePlatformService: ChallengePlatformService) {}

  ngOnInit(): void {
    if (this.challenge) {
      this.status = this.challenge.status ? this.challenge.status : 'No Status';
      this.statusClass = this.challenge.status || '';
      // this.difficulty = this.challenge.difficulty
      //   ? startCase(this.challenge.difficulty.replace('-', ''))
      //   : undefined;
      this.platform = this.challenge.platform;
      this.incentives =
        this.challenge.incentives.length === 0
          ? 'No incentives listed'
          : this.challenge.incentives
              .map(function (s) {
                return (
                  s[0].toUpperCase() +
                  s.substring(1).replace('_', ' ').toLowerCase()
                );
              })
              .join(', ');
    }
  }
}
