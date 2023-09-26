import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { GoogleTagManagerService } from 'angular-google-tag-manager';

@Component({
  selector: 'openchallenges-google-tag-manager',
  template: '',
  standalone: true,
})
export class GoogleTagManagerComponent implements OnInit {
  constructor(
    @Inject(PLATFORM_ID) private readonly platformId: object,
    private router: Router,
    private gtmService: GoogleTagManagerService
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
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
