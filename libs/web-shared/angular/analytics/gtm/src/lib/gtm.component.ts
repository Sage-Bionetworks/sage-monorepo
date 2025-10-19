import { Component, inject, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { GoogleTagManagerService } from 'angular-google-tag-manager';
import { GTM_CONFIG, GtmConfig } from './gtm.tokens';

@Component({
  selector: 'web-shared-angular-analytics-gtm',
  template: '',
  standalone: true,
  providers: [
    {
      provide: GoogleTagManagerService,
      useClass: GoogleTagManagerService,
    },
  ],
})
export class GoogleTagManagerComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly gtmService = inject(GoogleTagManagerService);
  private readonly gtmConfig = inject<GtmConfig>(GTM_CONFIG);

  ngOnInit(): void {
    if (!this.gtmConfig.isPlatformServer) {
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
