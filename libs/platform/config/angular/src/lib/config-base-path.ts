import { InjectionToken } from '@angular/core';

/**
 * App-specific config directory path for server-side YAML loading during development.
 * In production, relative paths from the server bundle work automatically.
 */
export const CONFIG_BASE_PATH = new InjectionToken<string>('CONFIG_BASE_PATH');
