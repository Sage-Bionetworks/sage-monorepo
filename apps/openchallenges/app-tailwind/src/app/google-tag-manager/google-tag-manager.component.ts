import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { GoogleTagManagerService } from 'angular-google-tag-manager';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { googleTagManagerIdProvider } from './google-tag-manager-id.provider';

@Component({
  selector: 'openchallenges-google-tag-manager',
  template: '',
  standalone: true,
  providers: [
    googleTagManagerIdProvider,
    // GoogleTagManagerService has been evaluated before we defined the GTM ID. That is why we
    // redefine it below so that its initialization get access to the GTM ID.
    {
      provide: GoogleTagManagerService,
      useClass: GoogleTagManagerService,
    },
  ],
})
export class GoogleTagManagerComponent implements OnInit {
  constructor(
    private router: Router,
    private gtmService: GoogleTagManagerService,
    private configService: ConfigService,
  ) {}

  ngOnInit(): void {
    if (!this.configService.config.isPlatformServer) {
      this.router.events.forEach((event) => {
        if (event instanceof NavigationEnd) {
          const gtmTag = {
            event: 'page',
            pageName: event.url,
          };
          this.gtmService.pushTag(gtmTag);
        }
      });
    }
  }
}
