import { HttpContext } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { SUPPRESS_ERROR_OVERLAY } from './http-context-tokens';
import { EMPTY, map, Observable, of } from 'rxjs';
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

  getDataVersion$(dataVersionService: DataVersionService): Observable<string> {
    if (this.platformService.isServer) {
      return EMPTY;
    }
    const context = new HttpContext().set(SUPPRESS_ERROR_OVERLAY, true);
    return dataVersionService
      .getDataVersion('body', false, { context })
      .pipe(map((data) => this.formatDataVersion(data)));
  }

  getSiteVersion$(config: VersionConfig): Observable<string> {
    if (this.platformService.isServer) {
      return EMPTY;
    }
    return of(this.formatSiteVersion(config.commitSha, config));
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
