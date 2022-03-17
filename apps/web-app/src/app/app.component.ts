import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { PageTitleService } from '@challenge-registry/web/util';
import {
  Avatar,
  MenuItem,
  MOCK_AVATAR_32,
  NavbarSection,
} from '@challenge-registry/web/ui';
import { APP_SECTIONS } from './app-sections';
import { AuthService } from '@challenge-registry/web/auth';
import { Router } from '@angular/router';
import { User } from '@challenge-registry/api-angular';

@Component({
  selector: 'challenge-registry-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'web-app';
  sections: { [key: string]: NavbarSection } = APP_SECTIONS;
  loggedIn = false;
  user!: User | undefined;
  userAvatar: Avatar = MOCK_AVATAR_32;
  userMenuItems: MenuItem[] = [
    {
      name: 'Profile',
      icon: 'account_circle',
    },
    {
      name: 'Log out',
      icon: 'exit_to_app',
    },
  ];

  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private pageTitleService: PageTitleService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    const loggedInSub = this.authService
      .isLoggedIn()
      .subscribe((loggedIn) => (this.loggedIn = loggedIn));
    this.subscriptions.push(loggedInSub);

    const userSub = this.authService.getUser().subscribe((user) => {
      this.user = user;
      if (user) {
        this.userAvatar.name = user.name ? user.name : user.login;
        this.userAvatar.src = user.avatarUrl ? user.avatarUrl : '';
      } else {
        this.userAvatar.name = '';
        this.userAvatar.src = '';
      }
    });
    this.subscriptions.push(userSub);

    this.pageTitleService.setTitle('Challenge Registry');
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  selectUserMenuItem(menuItem: MenuItem): void {
    // TODO DRY selected item, no not make comparison with string that way
    if (menuItem.name === 'Log out') {
      this.authService.logout();
    } else if (menuItem.name === 'Profile') {
      this.router.navigate([this.user?.login]);
    }
  }
}
