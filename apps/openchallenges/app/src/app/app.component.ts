import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ConfigService } from '@sagebionetworks/openchallenges/web/angular/config';
import { HomeDataService } from '@sagebionetworks/openchallenges/home';
import {
  Avatar,
  MenuItem,
  MOCK_AVATAR_32,
  NavbarComponent,
  NavbarSection,
  USER_MENU_ITEMS,
} from '@sagebionetworks/openchallenges/ui';
import { PageTitleService } from '@sagebionetworks/openchallenges/util';
import { GtmComponent } from '@sagebionetworks/web-shared/angular/analytics/gtm';
import { Subscription } from 'rxjs';
import { APP_SECTIONS } from './app-sections';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [NavbarComponent, RouterOutlet, GtmComponent],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'OpenChallenges';
  sections: { [key: string]: NavbarSection } = APP_SECTIONS;
  isLoggedIn = false;
  userAvatar: Avatar = MOCK_AVATAR_32;
  userMenuItems: MenuItem[] = USER_MENU_ITEMS;
  readonly useGoogleTagManager: boolean;

  private subscriptions: Subscription[] = [];

  private pageTitleService = inject(PageTitleService);
  private homeDataService = inject(HomeDataService);
  private configService = inject(ConfigService);

  constructor() {
    this.useGoogleTagManager = this.configService.config.google.tagManager.enabled;
  }

  ngOnInit() {
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
