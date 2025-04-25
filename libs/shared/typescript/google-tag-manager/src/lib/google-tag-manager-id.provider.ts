import { InjectionToken, Provider } from '@angular/core';

export interface ConfigWithGtm {
  googleTagManagerId: string;
  isPlatformServer?: boolean;
}

export interface ConfigService<T extends ConfigWithGtm = ConfigWithGtm> {
  readonly config: T;
  isPlatformServer?: boolean;
}

/**
 * Checks if a Google Tag Manager ID is set, i.e. not empty or whitespace-only string, null, or undefined.
 *
 * @param id The Google Tag Manager ID to check
 * @returns true if the ID is set (contains at least one non-whitespace character), false otherwise
 */
export function isGoogleTagManagerIdSet(id: string | undefined | null): boolean {
  return !!id?.trim();
}

export const CONFIG_SERVICE_TOKEN = new InjectionToken<ConfigService>('ConfigService');
export const createGoogleTagManagerIdProvider = <T extends ConfigWithGtm>(): Provider => ({
  provide: 'googleTagManagerId',
  useFactory: (configService: ConfigService<T>) => configService.config.googleTagManagerId,
  deps: [CONFIG_SERVICE_TOKEN],
});
