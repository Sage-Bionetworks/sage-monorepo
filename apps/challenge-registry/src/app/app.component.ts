import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription, filter, map, mergeMap } from 'rxjs';
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
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';
import { User } from '@sagebionetworks/api-angular';
import { SeoService } from '@sagebionetworks/shared/util';

@Component({
  selector: 'challenge-registry-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Challenge Registry';
  sections: { [key: string]: NavbarSection } = APP_SECTIONS;
  isLoggedIn = true;
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
    private activatedRoute: ActivatedRoute,
    private seoService: SeoService
  ) {}

  ngOnInit() {
    this.router.events
      .pipe(
        filter((e) => e instanceof NavigationEnd),
        map(() => this.activatedRoute),
        map((route) => {
          while (route.firstChild) route = route.firstChild;
          return route;
        }),
        filter((route) => route.outlet === 'primary'),
        mergeMap((route) => route.data)
      )
      .subscribe((data) => {
        if ('seo' in data) {
          const seoData = data['seo'];
          if (Object.prototype.hasOwnProperty.call(seoData, 'title')) {
            this.seoService.updateTitle(seoData['title']);
          }
          if (Object.prototype.hasOwnProperty.call(seoData, 'metaTags')) {
            this.seoService.updateMetaTags(seoData['metaTags']);
          }
        }
      });

    //     this.keycloakService
    // >>>>>>> main
    //       .isLoggedIn()
    //       .subscribe((isLoggedIn) => (this.isLoggedIn = isLoggedIn));

    this.kauthService.getUserProfile().subscribe((userProfile) => {
      this.userAvatar.name = userProfile.username ? userProfile.username : '';
    });

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
    this.kauthService.login();
  }
}
