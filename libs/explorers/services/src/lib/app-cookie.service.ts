import { Injectable, inject } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { PlatformService } from './platform.service';

@Injectable({
  providedIn: 'root',
})
export class AppCookieService {
  private readonly cookieService = inject(CookieService);
  private readonly platformService = inject(PlatformService);

  private readonly HIDE_VISUALIZATION_OVERVIEW_COOKIE = 'hide_visualization_overview';

  isVisualizationOverviewHidden(): boolean {
    if (this.platformService.isServer) {
      return false;
    }

    return this.cookieService.get(this.HIDE_VISUALIZATION_OVERVIEW_COOKIE) === '1';
  }

  setVisualizationOverviewHidden(hidden: boolean): void {
    if (this.platformService.isServer) {
      return;
    }

    if (hidden) {
      this.cookieService.set(this.HIDE_VISUALIZATION_OVERVIEW_COOKIE, '1', { path: '/' });
      return;
    }

    this.cookieService.delete(this.HIDE_VISUALIZATION_OVERVIEW_COOKIE, '/');
  }
}
