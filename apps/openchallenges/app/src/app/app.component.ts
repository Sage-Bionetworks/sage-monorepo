import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { PageTitleService } from '@sagebionetworks/openchallenges/util';
import {
  Avatar,
  MenuItem,
  MOCK_AVATAR_32,
  USER_MENU_ITEMS,
  NavbarSection,
  NavbarComponent,
} from '@sagebionetworks/openchallenges/ui';
import { APP_SECTIONS } from './app-sections';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { GoogleTagManagerService } from 'angular-google-tag-manager';
import { HomeDataService } from '@sagebionetworks/openchallenges/home';

@Component({
  selector: 'openchallenges-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  standalone: true,
  imports: [NavbarComponent, RouterOutlet],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'OpenChallenges';
  sections: { [key: string]: NavbarSection } = APP_SECTIONS;
  isLoggedIn = false;
  userAvatar: Avatar = MOCK_AVATAR_32;
  userMenuItems: MenuItem[] = USER_MENU_ITEMS;

  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private pageTitleService: PageTitleService,
    private gtmService: GoogleTagManagerService,
    private homeDataService: HomeDataService
  ) {}

  ngOnInit() {
    this.router.events.forEach((event) => {
      if (event instanceof NavigationEnd) {
        const gtmTag = {
          event: 'page',
          pageName: event.url,
        };

        this.gtmService.pushTag(gtmTag);
      }
    });

    // TODO Call getUserProfile() only if the user is logged in, other wise an error is generated
    // when the page is rendered with SSR.
    // https://github.com/Sage-Bionetworks/sage-monorepo/issues/880#issuecomment-1318955348
    // this.kauthService.getUserProfile().subscribe((userProfile) => {
    //   this.userAvatar.name = userProfile.username ? userProfile.username : '';
    // });
    this.userAvatar.name = 'blank';

    this.pageTitleService.setTitle('OpenChallenges');

    this.homeDataService.setChallengesPerYear();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  selectUserMenuItem(menuItem: MenuItem): void {
    console.log('Menu item selected', menuItem);
  }

  login(): void {
    console.log('Clicked on log In');
  }
}
