import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { ConfigService } from '@sagebionetworks/agora/config';
import { GoogleTagManagerService } from 'angular-google-tag-manager';
import { googleTagManagerIdProvider } from './google-tag-manager-id.provider';

@Component({
  selector: 'app-google-tag-manager',
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
    private readonly router: Router,
    private readonly gtmService: GoogleTagManagerService,
    private readonly configService: ConfigService,
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
