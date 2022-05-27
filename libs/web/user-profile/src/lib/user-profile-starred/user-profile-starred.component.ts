import { DOCUMENT } from '@angular/common';
import { Component, Inject, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Challenge, User, UserService } from '@sagebionetworks/api-angular';
import { map, Subscription } from 'rxjs';

@Component({
  selector: 'challenge-registry-user-profile-starred',
  templateUrl: './user-profile-starred.component.html',
  styleUrls: ['./user-profile-starred.component.scss'],
})
export class UserProfileStarredComponent implements OnInit, OnDestroy {
  @Input() user!: User;
  @Input() loggedIn!: boolean;

  stars: Challenge[] = [];
  starred!: boolean;
  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private userService: UserService,
    @Inject(DOCUMENT) private document: Document
  ) {}

  ngOnInit(): void {
    const starsSub = this.userService
      .listUserStarredChallenges(this.user.id)
      .pipe(map((page) => page.challenges))
      .subscribe((stars) => (this.stars = stars));
    this.subscriptions.push(starsSub);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  onClick(url: string): void {
    // TODO: review logic
    if (!this.document.getSelection()?.toString() ?? false) {
      this.router.navigateByUrl(url);
    }
  }
}
