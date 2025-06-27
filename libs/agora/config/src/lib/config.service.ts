import { isPlatformServer } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, Optional, PLATFORM_ID } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { AppConfig, EMPTY_APP_CONFIG } from './app.config';

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  config: AppConfig = EMPTY_APP_CONFIG;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: string,
    @Inject('APP_PORT') @Optional() private readonly port: string,
  ) {}

  async loadConfig(): Promise<void> {
    const browserRoot = isPlatformServer(this.platformId) ? `http://localhost:${this.port}` : '.';

    const appConfig$ = this.http.get<AppConfig>(`${browserRoot}/config/config.json`);
    try {
      const config = await lastValueFrom(appConfig$);
      this.config = config;
      this.config.isPlatformServer = isPlatformServer(this.platformId);
    } catch (err) {
      console.error('Unable to load the config file: ', err);
      return await Promise.resolve();
    }
  }
}
