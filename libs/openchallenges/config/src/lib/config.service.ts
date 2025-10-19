import { inject, Injectable } from '@angular/core';
import { ConfigLoaderService } from './config-loader.service';
import { RuntimeAppConfig } from './config.schema';

/**
 * Main configuration service
 * Provides access to application configuration
 *
 * Usage:
 * 1. ConfigService is initialized via APP_INITIALIZER (see config.factory.ts)
 * 2. Inject ConfigService in components/services to access config
 * 3. Access config properties via configService.config
 */
@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  private readonly configLoader = inject(ConfigLoaderService);

  /**
   * Current application configuration
   * Access this property after APP_INITIALIZER has run
   */
  config!: RuntimeAppConfig;

  /**
   * Load configuration using the new YAML-based system
   * Called during APP_INITIALIZER phase
   */
  async loadConfig(): Promise<void> {
    try {
      this.config = await this.configLoader.loadConfig();
    } catch (err) {
      console.error('[ConfigService] Unable to load configuration:', err);
      // In case of error, use empty config to prevent app crash
      // The app should have sensible defaults or handle missing config gracefully
      throw err; // Re-throw to let app initialization know there's an issue
    }
  }

  /**
   * Reload configuration
   * Useful for development or when config needs to be refreshed
   */
  async reloadConfig(): Promise<void> {
    try {
      this.config = await this.configLoader.reloadConfig();
    } catch (err) {
      console.error('[ConfigService] Unable to reload configuration:', err);
      throw err;
    }
  }

  /**
   * Get a specific config value by path
   * Example: getValue('api.csr.url') returns config.api.csr.url
   */
  getValue<T = any>(path: string): T | undefined {
    const parts = path.split('.');
    let current: any = this.config;

    for (const part of parts) {
      if (current && typeof current === 'object' && part in current) {
        current = current[part];
      } else {
        return undefined;
      }
    }

    return current as T;
  }
}
