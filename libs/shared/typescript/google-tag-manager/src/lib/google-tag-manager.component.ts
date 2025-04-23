import { Component, Inject, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { GoogleTagManagerService } from 'angular-google-tag-manager';
import { CONFIG_SERVICE_TOKEN, ConfigService } from './google-tag-manager-id.provider';

@Component({
  selector: 'sage-google-tag-manager',
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
  constructor(
    private readonly router: Router,
    private readonly gtmService: GoogleTagManagerService,
    @Inject(CONFIG_SERVICE_TOKEN) private readonly configService: ConfigService,
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
