import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { GoogleTagManagerService } from 'angular-google-tag-manager';
import { googleTagManagerIdFactory } from './google-tag-manager-id.factory';
import { ConfigService } from '@sagebionetworks/openchallenges/config';

@Component({
  selector: 'openchallenges-google-tag-manager',
  template: '',
  standalone: true,
  providers: [
    {
      provide: 'googleTagManagerId',
      useFactory: googleTagManagerIdFactory,
      deps: [ConfigService],
    },
    {
      provide: GoogleTagManagerService,
      useClass: GoogleTagManagerService,
    },
  ],
})
export class GoogleTagManagerComponent implements OnInit {
  constructor(
    @Inject(PLATFORM_ID) private readonly platformId: object,
    private router: Router,
    @Inject('googleTagManagerId') private googleTagManagerId: string,
    private gtmService: GoogleTagManagerService
  ) {
    console.log(`constructor: ${googleTagManagerId}`);
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.router.events.forEach((event) => {
        if (event instanceof NavigationEnd) {
          const gtmTag = {
            event: 'page',
            pageName: event.url,
          };
          // const gtmConfig = this.gtmService.googleTagManagerConfiguration.get();
          // gtmConfig.id = this.googleTagManagerId;
          // this.gtmService.googleTagManagerConfiguration.set(gtmConfig);

          // this.gtmService.googleTagManagerId = this.googleTagManagerId;
          this.gtmService.pushTag(gtmTag);
        }
      });
    }
  }
}
