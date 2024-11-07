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
import { RouterOutlet } from '@angular/router';
import { HomeDataService } from '@sagebionetworks/openchallenges/home';
import { GoogleTagManagerComponent } from './google-tag-manager/google-tag-manager.component';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { NgIf } from '@angular/common';

@Component({
  selector: 'openchallenges-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  standalone: true,
  imports: [NavbarComponent, RouterOutlet, NgIf, GoogleTagManagerComponent],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'OpenChallenges';
  sections: { [key: string]: NavbarSection } = APP_SECTIONS;
  isLoggedIn = false;
  userAvatar: Avatar = MOCK_AVATAR_32;
  userMenuItems: MenuItem[] = USER_MENU_ITEMS;
  readonly useGoogleTagManager: boolean;

  private subscriptions: Subscription[] = [];

  constructor(
    private pageTitleService: PageTitleService,
    private homeDataService: HomeDataService,
    private configService: ConfigService,
  ) {
    this.useGoogleTagManager = this.configService.config.googleTagManagerId.length > 0;
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
