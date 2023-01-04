import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { PageTitleService } from '@sagebionetworks/challenge-registry/util';
import {
  Avatar,
  MenuItem,
  MOCK_AVATAR_32,
  USER_MENU_ITEMS,
  MOCK_USER,
  NavbarSection,
} from '@sagebionetworks/challenge-registry/ui';
import { APP_SECTIONS } from './app-sections';
import {
  KAuthService,
  AuthService,
} from '@sagebionetworks/challenge-registry/auth';
import { ActivatedRoute, Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';
import { User } from '@sagebionetworks/challenge-registry/api-client-angular-deprecated';

@Component({
  selector: 'challenge-registry-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Challenge Registry';
  sections: { [key: string]: NavbarSection } = APP_SECTIONS;
  isLoggedIn = false;
  user: User = MOCK_USER;
  userAvatar: Avatar = MOCK_AVATAR_32;
  userMenuItems: MenuItem[] = USER_MENU_ITEMS;

  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private pageTitleService: PageTitleService,
    private kauthService: KAuthService,
    private authService: AuthService,
    private keycloakService: KeycloakService,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit() {
    this.kauthService
      .isLoggedIn()
      .subscribe((isLoggedIn) => (this.isLoggedIn = isLoggedIn));

    // TODO Call getUserProfile() only if the user is logged in, other wise an error is generated
    // when the page is rendered with SSR.
    // https://github.com/Sage-Bionetworks/sage-monorepo/issues/880#issuecomment-1318955348
    // this.kauthService.getUserProfile().subscribe((userProfile) => {
    //   this.userAvatar.name = userProfile.username ? userProfile.username : '';
    // });
    this.userAvatar.name = 'blank';

    this.pageTitleService.setTitle('Challenge Registry');
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  selectUserMenuItem(menuItem: MenuItem): void {
    // TODO: DRY selected item, no not make comparison with string that way
    if (menuItem.name === 'Log out') {
      this.kauthService.logout();
    } else if (menuItem.name === 'Profile') {
      this.router.navigate([this.user?.login]);
    }
    // TODO: redirect to all tabs of profile when the rest of tabs components are created
  }

  login(): void {
    console.log('Clicked on log In');
    this.kauthService.login();
  }
}
