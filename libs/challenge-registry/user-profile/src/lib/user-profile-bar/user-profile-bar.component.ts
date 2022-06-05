import { Component, Input, OnInit } from '@angular/core';
import { map, Observable } from 'rxjs';
import { User, UserService } from '@sagebionetworks/api-angular';
import { Avatar } from '@sagebionetworks/challenge-registry/ui';

@Component({
  selector: 'challenge-registry-user-profile-bar',
  templateUrl: './user-profile-bar.component.html',
  styleUrls: ['./user-profile-bar.component.scss'],
})
export class UserProfileBarComponent implements OnInit {
  @Input() user!: User;
  @Input() userAvatar!: Avatar;
  @Input() numOrgs = 0;

  // mock up summary data
  isVerified = true;
  numStarredChallenges$!: Observable<number>;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userAvatar = {
      name: this.user.name
        ? (this.user.name as string)
        : this.user.login.replace(/-/g, ' '),
      src: this.user.avatarUrl ? this.user.avatarUrl : '',
      size: 160,
    };

    this.numStarredChallenges$ = this.userService
      .listUserStarredChallenges(this.user.id, 10, 0)
      .pipe(map((page) => page.totalResults));
  }
}
