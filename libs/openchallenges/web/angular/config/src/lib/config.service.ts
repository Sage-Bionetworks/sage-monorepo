import { Injectable } from '@angular/core';
import { ConfigLoaderService } from '@sagebionetworks/platform/config/angular';
import { RuntimeAppConfig, validateConfig } from './config.schema';

/**
 * OpenChallenges-specific configuration service
 * Extends the generic ConfigLoaderService with OpenChallenges schema validation
 *
 * Usage:
 * 1. ConfigService is initialized via APP_INITIALIZER (see config.factory.ts)
 * 2. Inject ConfigService in components/services to access config
 * 3. Access config properties via configService.config
 */
@Injectable({
  providedIn: 'root',
})
export class ConfigService extends ConfigLoaderService<RuntimeAppConfig> {
  /**
   * Current application configuration
   * Access this property after APP_INITIALIZER has run
   */
  config!: RuntimeAppConfig;

  /**
   * Validate configuration using OpenChallenges schema
   */
  protected override validateConfig(config: unknown): RuntimeAppConfig {
    const validated = validateConfig(config);
    return {
      ...validated,
      isPlatformServer: this.isServer,
      googleTagManagerId: validated.google.tagManager.id, // Expose for GTM compatibility
    };
  }

  /**
   * Load configuration using the new YAML-based system
   * Called during APP_INITIALIZER phase
   */
  override async loadConfig(basePath?: string): Promise<RuntimeAppConfig> {
    const loaded = await super.loadConfig(basePath);
    this.config = loaded;
    return loaded;
  }

  /**
   * Reload configuration
   * Useful for development or when config needs to be refreshed
   */
  override async reloadConfig(): Promise<RuntimeAppConfig> {
    const reloaded = await super.reloadConfig();
    this.config = reloaded;
    return reloaded;
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
