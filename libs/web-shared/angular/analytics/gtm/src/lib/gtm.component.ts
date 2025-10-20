import { Component, inject, OnInit, Injector } from '@angular/core';
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
  private readonly injector = inject(Injector);
  private readonly gtmConfig = inject<GtmConfig>(GTM_CONFIG, { optional: true });

  // Lazily initialized GTM service (only created when needed)
  private gtmService?: GoogleTagManagerService;

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

    // Lazy initialization: inject GTM service only when we're ready to use it
    // This happens AFTER config has loaded (because ngOnInit runs after APP_INITIALIZER)
    try {
      this.gtmService = this.injector.get(GoogleTagManagerService, undefined);

      if (!this.gtmService) {
        console.error('GoogleTagManagerService could not be instantiated');
        return;
      }

      console.info('GTM service initialized with ID:', this.gtmConfig.gtmId);
    } catch (error) {
      console.error('Failed to initialize GTM service:', error);
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
