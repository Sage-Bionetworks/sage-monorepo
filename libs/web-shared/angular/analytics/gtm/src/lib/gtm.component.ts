import { Component, inject, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { GoogleTagManagerService } from 'angular-google-tag-manager';
import { GTM_CONFIG, GtmConfig, isGtmIdSet } from './gtm.tokens';

@Component({
  selector: 'web-shared-angular-analytics-gtm',
  template: '',
  standalone: true,
})
export class GtmComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly gtmService = inject(GoogleTagManagerService, { optional: true });
  private readonly gtmConfig = inject<GtmConfig>(GTM_CONFIG, { optional: true });

  ngOnInit(): void {
    // Only proceed if config is provided and GTM ID is set
    if (!this.gtmConfig || !isGtmIdSet(this.gtmConfig.gtmId)) {
      console.warn('GTM component initialized but no valid GTM configuration provided');
      return;
    }

    // Check if GTM is enabled
    if (!this.gtmConfig.enabled) {
      console.info('GTM is disabled in configuration');
      return;
    }

    // Only run on client side
    if (this.gtmConfig.isPlatformServer) {
      return;
    }

    // Only proceed if GTM service is available
    if (!this.gtmService) {
      console.error('GoogleTagManagerService not provided. Did you call provideGoogleTagManager?');
      return;
    }

    // Track page navigation
    this.router.events.forEach((event) => {
      if (event instanceof NavigationEnd && this.gtmService) {
        const gtmTag = {
          event: 'page',
          pageName: event.url,
        };
        this.gtmService.pushTag(gtmTag).catch((error) => {
          // Silently handle GTM script load failures (ad blockers, certificate errors, etc.)
          // This is expected in development or when GTM is blocked
          console.debug('GTM tracking skipped:', error || 'Script load failed');
        });
      }
    });
  }
}
