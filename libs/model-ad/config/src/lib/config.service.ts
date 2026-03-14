import { isPlatformServer } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable, PLATFORM_ID } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { getCachedConfig } from '@sagebionetworks/explorers/util';
import { APP_PORT, AppConfig, EMPTY_APP_CONFIG } from './app.config';

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  private readonly http = inject(HttpClient);
  private readonly platformId: Record<string, any> = inject(PLATFORM_ID);
  private readonly port = inject(APP_PORT, { optional: true });

  config: AppConfig = EMPTY_APP_CONFIG;

  async loadConfig(): Promise<void> {
    const cached = getCachedConfig<AppConfig>();
    if (cached) {
      this.config = cached;
      this.config.isPlatformServer = isPlatformServer(this.platformId);
      return;
    }

    const browserRoot = isPlatformServer(this.platformId) ? `http://localhost:${this.port}` : '.';

    try {
      const config = await lastValueFrom(
        this.http.get<AppConfig>(`${browserRoot}/config/config.json`),
      );
      this.config = config;
      this.config.isPlatformServer = isPlatformServer(this.platformId);
    } catch (err) {
      console.error('Unable to load the config file: ', err);
    }
  }
}
