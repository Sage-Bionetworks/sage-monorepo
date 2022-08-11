import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, Optional } from '@angular/core';
import { AppConfig } from './app.config';
// import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  config?: AppConfig;

  constructor(
    private http: HttpClient,
    @Inject('APP_BASE_URL') @Optional() private readonly baseUrl: string
  ) {}

  loadConfig(): Promise<void> {
    console.log('baseUrl', this.baseUrl);

    return (
      this.http
        .get<AppConfig>(`${this.baseUrl}/config/config.json`)
        // .pipe(tap((config) => (this.config = config)));
        .toPromise()
        .then((config) => {
          this.config = config;
        })
        .catch(() => Promise.resolve())
    );
  }
}
