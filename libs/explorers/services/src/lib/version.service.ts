import { HttpContext } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { SUPPRESS_ERROR_OVERLAY } from '@sagebionetworks/explorers/constants';
import { catchError, EMPTY, map, Observable, of, throwError } from 'rxjs';
import { GitHubService } from './github.service';
import { LoggerService } from './logger.service';
import { PlatformService } from './platform.service';

export interface VersionConfig {
  appVersion: string;
  tagName: string;
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
  private readonly gitHubService = inject(GitHubService);

  getDataVersion$(dataVersionService: DataVersionService): Observable<string> {
    if (this.platformService.isServer) {
      return EMPTY;
    }
    const context = new HttpContext().set(SUPPRESS_ERROR_OVERLAY, true);
    return dataVersionService.getDataVersion('body', false, { context }).pipe(
      map((data) => this.formatDataVersion(data)),
      catchError((error) => {
        this.logger.error('Error loading data version', error);
        return throwError(() => error);
      }),
    );
  }

  getSiteVersion$(config: VersionConfig): Observable<string> {
    if (this.platformService.isServer) {
      return EMPTY;
    }
    return this.gitHubService.getCommitSHA(config.tagName).pipe(
      map((sha) => this.formatSiteVersion(sha, config)),
      catchError((error) => {
        this.logger.error('Error loading commit SHA', error);
        return of(this.formatSiteVersion('', config));
      }),
    );
  }

  formatDataVersion(dataVersion: DataVersion): string {
    return `${dataVersion.data_file}-v${dataVersion.data_version}`;
  }

  formatSiteVersion(sha: string, config: VersionConfig): string {
    const appVersion = this.formatAppVersion(config.appVersion);
    if (!appVersion) {
      return sha;
    }
    return sha ? `${appVersion}-${sha}` : appVersion;
  }

  formatAppVersion(appVersion: string): string {
    if (!appVersion) {
      return '';
    }
    // remove the -rcX suffix
    return appVersion.replace(/-rc\d+$/, '');
  }
}
