import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription, from } from 'rxjs';
import { PageTitleService } from '@sagebionetworks/challenge-registry/util';
import {
  Avatar,
  MenuItem,
  MOCK_AVATAR_32,
  MOCK_MENU_ITEMS,
  MOCK_USER,
  NavbarSection,
} from '@sagebionetworks/challenge-registry/ui';
import { APP_SECTIONS } from './app-sections';
import { AuthService } from '@sagebionetworks/challenge-registry/auth';
import { Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';
import { User } from '@sagebionetworks/api-angular';

@Component({
  selector: 'challenge-registry-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Challenge Registry';
  sections: { [key: string]: NavbarSection } = APP_SECTIONS;
  loggedIn = true;
  user: User = MOCK_USER;
  userAvatar: Avatar = MOCK_AVATAR_32;
  userMenuItems: MenuItem[] = MOCK_MENU_ITEMS;

  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private pageTitleService: PageTitleService,
    private authService: AuthService,
    private keycloakService: KeycloakService
  ) {}

  ngOnInit() {
    this.keycloakService
      .isLoggedIn()
      .then((loggedIn) => {
        if (loggedIn) {
          console.log(this.keycloakService.getUsername());
        }
      })
      .catch((reason) => console.log(reason));

    // console.log(this.keycloakService.getUsername());
    // const userDetails = await this.keycloakService.loadUserProfile();
    // console.log(userDetails);

    from(this.keycloakService.isLoggedIn()).subscribe(
      (isLoggedIn) => {
        if (isLoggedIn) {
          // console.log(this.keycloakService.getUsername());
          console.log('isLoggedIn: ', isLoggedIn);
        }
      }
      // (loggedIn) => (this.loggedIn = loggedIn)
    );

    // const loggedInSub = this.authService
    //   .isLoggedIn()
    //   .subscribe((loggedIn) => (this.loggedIn = loggedIn));
    // this.subscriptions.push(loggedInSub);

    // const userSub = this.authService.getUser().subscribe((user) => {
    //   this.user = user;
    //   if (user) {
    //     this.userAvatar.name = user.name ? user.name : user.login;
    //     this.userAvatar.src = user.avatarUrl ? user.avatarUrl : '';
    //   } else {
    //     this.userAvatar.name = '';
    //     this.userAvatar.src = '';
    //   }
    // });
    // this.subscriptions.push(userSub);

    this.pageTitleService.setTitle('Challenge Registry');
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  selectUserMenuItem(menuItem: MenuItem): void {
    // TODO DRY selected item, no not make comparison with string that way
    if (menuItem.name === 'Log out') {
      // this.authService.logout();
      this.keycloakService.logout();
    } else if (menuItem.name === 'Profile') {
      this.router.navigate([this.user?.login]);
    }
    // TODO: redirect to all tabs of profile when the rest of tabs components are created
  }
}
