import { Component, Input } from '@angular/core';
// import { Challenge } from '@sagebionetworks/api-client-angular';

@Component({
  selector: 'challenge-registry-challenge-header',
  templateUrl: './challenge-header.component.html',
  styleUrls: ['./challenge-header.component.scss'],
})
export class ChallengeHeaderComponent {
  @Input() accountName = '';
  // @Input() challenge!: Challenge;
}
