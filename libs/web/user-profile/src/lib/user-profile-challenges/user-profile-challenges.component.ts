import { Component, Input, OnInit } from '@angular/core';
import { Challenge, UserService } from '@sage-bionetworks/api-angular';
import { map, Observable } from 'rxjs';

@Component({
  selector: 'challenge-registry-user-profile-challenges',
  templateUrl: './user-profile-challenges.component.html',
  styleUrls: ['./user-profile-challenges.component.scss'],
})
export class UserProfileChallengesComponent implements OnInit {
  @Input() userId!: string;
  challenges$!: Observable<Challenge[]>;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.challenges$ = this.userService
      .listUserStarredChallenges(this.userId)
      .pipe(map((page) => page.challenges));
  }
}
