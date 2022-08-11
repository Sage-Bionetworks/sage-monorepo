import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppConfig } from './app.config';
// import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  config?: AppConfig;

  constructor(private http: HttpClient) {}

  loadConfig() {
    return (
      this.http
        .get<AppConfig>('config/config.json')
        // .pipe(tap((config) => (this.config = config)));
        .toPromise()
        .then((config) => {
          this.config = config;
        })
        .catch((error) => {
          console.error(error);
        })
    );
  }
}
