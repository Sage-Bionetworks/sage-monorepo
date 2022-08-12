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
    @Inject('APP_BASE_URL') @Optional() private readonly baseUrl: string
  ) {}

  loadConfig(): Promise<void> {
    const appConfig$ = this.http.get<AppConfig>(
      `${this.baseUrl}/config/config.json`
    );
    return lastValueFrom(appConfig$)
      .then((config) => {
        this.config = config;
        this.config.isPlatformServer = isPlatformServer(this.platformId);
      })
      .catch((err) => {
        console.error('Unable to load the config file: ', err);
        return Promise.resolve();
      });
  }
}
