import { Component, OnInit } from '@angular/core';
import { ChallengeDataService } from '../challenge-data.service';
import { ChallengeService } from '@challenge-registry/api-angular';

@Component({
  selector: 'challenge-registry-challenge-sponsors',
  templateUrl: './challenge-sponsors.component.html',
  styleUrls: ['./challenge-sponsors.component.scss'],
})
export class ChallengeSponsorsComponent implements OnInit {
  constructor(
    private challengeDataService: ChallengeDataService,
    private challengeService: ChallengeService
  ) {}

  ngOnInit(): void {
    this.challengeDataService.getChallenge().subscribe(console.log);
  }
}
