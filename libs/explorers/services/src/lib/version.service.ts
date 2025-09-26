import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Observable } from 'rxjs';
import { GitHubService } from './github.service';
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
  getDataVersion(): Observable<DataVersion>;
}

@Injectable({
  providedIn: 'root',
})
export class VersionService {
  private readonly platformService = inject(PlatformService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly gitHubService = inject(GitHubService);

  getDataVersion(
    dataVersionService: DataVersionService,
    onSuccess: (dataVersion: string) => void,
    onError?: (error: any) => void,
  ): void {
    if (this.platformService.isBrowser) {
      dataVersionService
        .getDataVersion()
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (data) => {
            const formattedVersion = this.formatDataVersion(data);
            onSuccess(formattedVersion);
          },
          error: (error) => {
            console.error('Error loading data version:', error);
            if (onError) {
              onError(error);
            }
          },
        });
    }
  }

  async getSiteVersion(
    config: VersionConfig,
    onSuccess: (siteVersion: string) => void,
    onError?: (error: any) => void,
  ): Promise<void> {
    if (this.platformService.isBrowser) {
      try {
        const sha = await this.gitHubService.getCommitSHA(config.tagName);
        const siteVersion = this.formatSiteVersion(sha, config);
        onSuccess(siteVersion);
      } catch (error) {
        console.error('Error loading commit SHA:', error);
        const fallbackVersion = this.formatSiteVersion('', config);
        if (fallbackVersion) {
          onSuccess(fallbackVersion);
        } else {
          console.error('Error loading appVersion');
          if (onError) {
            onError(error);
          }
        }
      }
    }
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
