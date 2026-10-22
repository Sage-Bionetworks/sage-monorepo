import { HttpContext } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AppVersion } from '@sagebionetworks/explorers/config';
import {
  DATA_VERSION_LOADING,
  DATA_VERSION_UNKNOWN,
  VERSION_CHECK_TIMEOUT_MS,
} from '@sagebionetworks/explorers/constants';
import { SKIP_ERROR_REPORTING, SUPPRESS_ERROR_OVERLAY } from './http-context-tokens';
import { catchError, map, Observable, of, startWith, tap, timeout, TimeoutError } from 'rxjs';
import { LoggerService } from './logger.service';
import { PlatformService } from './platform.service';

export interface DataVersion {
  data_file: string;
  data_version: string;
  team_images_id?: string;
}

/** A data version is known only when both its data file and its version number are set. */
export function isKnownDataVersion({
  data_file: dataFile,
  data_version: dataVersion,
}: DataVersion) {
  return Boolean(dataFile) && Boolean(dataVersion);
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
  private readonly logger = inject(LoggerService).forSource('VersionService');
  private loadedDataVersion: DataVersion | null = null;

  /**
   * Emits DATA_VERSION_LOADING synchronously on subscribe, then the formatted data version, or
   * DATA_VERSION_UNKNOWN if the request fails or does not answer within `VERSION_CHECK_TIMEOUT_MS`.
   * During SSR it emits only DATA_VERSION_LOADING.
   * The synchronous first emission supports `toSignal(..., { requireSync: true })`; callers that
   * need the resolved value must read the last emission, not the first.
   */
  getDataVersion$(dataVersionService: DataVersionService): Observable<string> {
    if (this.platformService.isServer) {
      return of(DATA_VERSION_LOADING);
    }
    const context = new HttpContext()
      .set(SUPPRESS_ERROR_OVERLAY, true)
      .set(SKIP_ERROR_REPORTING, true);
    return dataVersionService.getDataVersion('body', false, { context }).pipe(
      timeout(VERSION_CHECK_TIMEOUT_MS),
      tap((dataVersion) => this.recordLoadedDataVersion(dataVersion)),
      map((dataVersion) => this.formatDataVersion(dataVersion)),
      catchError((error) => {
        // A stalled connection, like an offline one, is not a bug worth a Sentry event.
        if (error instanceof TimeoutError) {
          this.logger.log('Data version request timed out', {
            timeoutMs: VERSION_CHECK_TIMEOUT_MS,
          });
        } else {
          this.logger.warn('Failed to fetch data version', { error });
        }
        return of(DATA_VERSION_UNKNOWN);
      }),
      startWith(DATA_VERSION_LOADING),
    );
  }

  /**
   * The data version this tab loaded with, which `AppUpdateService` compares the server's against.
   * Null until the footer's data version request or the first data version check succeeds.
   */
  getLoadedDataVersion(): DataVersion | null {
    return this.loadedDataVersion;
  }

  /**
   * Keeps only the first data version loaded, whether by the footer's request or, when that
   * request failed or timed out, by `AppUpdateService`'s first data version check. The page shows
   * data from the release that was current when it loaded, so that release is what a newer one
   * must be compared against. A later request that saw a newer release must not overwrite it, or
   * the change would go unnoticed.
   */
  recordLoadedDataVersion(dataVersion: DataVersion) {
    // An unknown version would be kept for good and could never be compared, which would turn off
    // data release detection for the tab, so the slot stays empty for a later request to fill.
    if (this.loadedDataVersion === null && isKnownDataVersion(dataVersion)) {
      this.loadedDataVersion = dataVersion;
    }
  }

  formatDataVersion(dataVersion: DataVersion): string {
    return `${dataVersion.data_file}-v${dataVersion.data_version}`;
  }

  getSiteVersion(config: AppVersion): string {
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
