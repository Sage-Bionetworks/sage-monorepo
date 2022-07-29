import { Component, Input } from '@angular/core';
import { Challenge } from '@sagebionetworks/api-angular';

@Component({
  selector: 'challenge-registry-challenge-card',
  templateUrl: './challenge-card.component.html',
  styleUrls: ['./challenge-card.component.scss'],
})
export class ChallengeCardComponent {
  @Input() challenge!: Challenge;
}
