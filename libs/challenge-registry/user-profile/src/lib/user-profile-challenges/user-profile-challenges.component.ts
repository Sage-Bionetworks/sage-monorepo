import { Component, Input } from '@angular/core';
import { Challenge, User, UserService } from '@sagebionetworks/api-angular';
import { MOCK_CHALLENGES } from '@sagebionetworks/challenge-registry/ui';
// import { map, Observable } from 'rxjs';

@Component({
  selector: 'challenge-registry-user-profile-challenges',
  templateUrl: './user-profile-challenges.component.html',
  styleUrls: ['./user-profile-challenges.component.scss'],
})
export class UserProfileChallengesComponent {
  @Input() user!: User;
  challenges: Challenge[] = MOCK_CHALLENGES;

  constructor(private userService: UserService) {}
}
