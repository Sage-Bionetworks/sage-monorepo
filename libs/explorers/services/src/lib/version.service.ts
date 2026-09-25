import { HttpContext } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { DATA_VERSION_LOADING, DATA_VERSION_UNKNOWN } from '@sagebionetworks/explorers/constants';
import { SKIP_ERROR_REPORTING, SUPPRESS_ERROR_OVERLAY } from './http-context-tokens';
import { catchError, map, Observable, of, startWith } from 'rxjs';
import { LoggerService } from './logger.service';
import { PlatformService } from './platform.service';

export interface VersionConfig {
  appVersion: string;
  commitSha: string;
}

export interface DataVersion {
  data_file: string;
  data_version: string;
  team_images_id?: string;
}

export interface DataVersionService {
  getDataVersion(
    observe?: 'body',
    reportProgress?: boolean,
    options?: { context?: HttpContext },
  ): Observable<DataVersion>;
}

@Injectable({
  providedIn: 'root',
})
export class VersionService {
  private readonly platformService = inject(PlatformService);
  private readonly logger = inject(LoggerService);

  getDataVersion$(dataVersionService: DataVersionService): Observable<string> {
    if (this.platformService.isServer) {
      return of(DATA_VERSION_LOADING);
    }
    const context = new HttpContext()
      .set(SUPPRESS_ERROR_OVERLAY, true)
      .set(SKIP_ERROR_REPORTING, true);
    return dataVersionService.getDataVersion('body', false, { context }).pipe(
      map((data) => this.formatDataVersion(data)),
      catchError((error) => {
        this.logger.warn('Failed to fetch data version', { error });
        return of(DATA_VERSION_UNKNOWN);
      }),
      startWith(DATA_VERSION_LOADING),
    );
  }

  formatDataVersion(dataVersion: DataVersion): string {
    return `${dataVersion.data_file}-v${dataVersion.data_version}`;
  }

  getSiteVersion(config: VersionConfig): string {
    const appVersion = this.formatAppVersion(config.appVersion);
    if (!appVersion) {
      return config.commitSha;
    }
    return config.commitSha ? `${appVersion}-${config.commitSha}` : appVersion;
  }

  formatAppVersion(appVersion: string): string {
    if (!appVersion) {
      return '';
    }
    // remove the -rcX suffix
    return appVersion.replace(/-rc\d+$/, '');
  }
}
