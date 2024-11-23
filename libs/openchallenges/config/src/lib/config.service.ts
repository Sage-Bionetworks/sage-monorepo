import { isPlatformBrowser, isPlatformServer } from '@angular/common';
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
    // The location of the browser folder, which includes the config folder, depends on whether the
    // present code is run in the browser and by the node server. We could use the request received
    // by the node server (SSR) and get the host and port, but this does not work when the port is
    // different from the Angular or node port. Note that `localhost` works inside the container
    // (production) as well as outside (dev server). APP_BASE_URL could be used when running the dev
    // server and accessing it directly (e.g. APP_BASE_URL = 'http://localhost:37677') but not when
    // accessing the production server in the container from apex (APP_BASE_URL =
    // 'http://localhost:8000', which is invalid inside the container).

    // console.log(`isPlatformBrowser: ${isPlatformBrowser(this.platformId)}`);
    // console.log(`isPlatformServer: ${isPlatformServer(this.platformId)}`);
    // console.log(`config service port: ${this.port}`);
    // const browserRoot = isPlatformServer(this.platformId) ? `http://localhost:${this.port}` : '.';
    const browserRoot = '';

    const appConfig$ = this.http.get<AppConfig>(`${browserRoot}/config/config.json`);
    try {
      const config = await lastValueFrom(appConfig$);
      this.config = config;
      console.log(`config: ${this.config.appVersion}`);
      this.config.isPlatformServer = isPlatformServer(this.platformId);
      this.config.privacyPolicyUrl =
        'https://sagebionetworks.jira.com/wiki/spaces/OA/pages/2948530178/OpenChallenges+Privacy+Policy';
      this.config.termsOfUseUrl =
        'https://sagebionetworks.jira.com/wiki/spaces/OA/pages/2948333575/OpenChallenges+Terms+of+Use';
    } catch (err) {
      console.error('Unable to load the config file: ', err);
      return await Promise.resolve();
    }
  }
}
