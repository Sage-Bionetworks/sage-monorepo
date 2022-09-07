import { Component, Input, OnInit } from '@angular/core';
import { Challenge } from '@sagebionetworks/api-angular';

@Component({
  selector: 'challenge-registry-challenge-card',
  templateUrl: './challenge-card.component.html',
  styleUrls: ['./challenge-card.component.scss'],
})
export class ChallengeCardComponent implements OnInit {
  @Input() challenge!: Challenge;
  // tmp platform
  platform = 'Platform';
  status!: string | undefined;
  difficulty!: string | undefined;

  ngOnInit(): void {
    if (this.challenge) {
      this.status = this.challenge.status ? this.challenge.status : 'No Status';
      this.difficulty = this.challenge.difficulty
        ? this.challenge.difficulty.replace(/([a-z])([A-Z])/g, '$1 $2')
        : undefined;
    }
  }
}
