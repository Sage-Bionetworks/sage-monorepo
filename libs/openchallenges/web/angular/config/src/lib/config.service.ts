import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';
import { ConfigLoaderService } from '@sagebionetworks/platform/config/angular';
import { RuntimeAppConfig, validateConfig, AppConfig } from './config.schema';
import { RuntimeClientConfig } from './client-config.schema';
import { transformServerToClientConfig } from './config-transformer';

/**
 * OpenChallenges-specific configuration service
 * Extends the generic ConfigLoaderService with:
 * - OpenChallenges schema validation
 * - Server/Client config separation
 * - Automatic transformation for browser
 *
 * Type Safety:
 * - On Server (SSR): Uses RuntimeServerConfig (full configuration)
 * - On Client (Browser): Uses RuntimeClientConfig (filtered subset)
 *
 * Usage:
 * 1. ConfigService is initialized via APP_INITIALIZER (see config.factory.ts)
 * 2. Inject ConfigService in components/services to access config
 * 3. Access config properties via configService.config
 *
 * The service automatically:
 * - Loads full config on server
 * - Transforms to client format before transfer
 * - Client receives only necessary properties
 */
@Injectable({
  providedIn: 'root',
})
export class ConfigService extends ConfigLoaderService<
  RuntimeClientConfig | RuntimeAppConfig,
  AppConfig
> {
  protected override readonly platformId = inject(PLATFORM_ID);

  /**
   * Current application configuration
   * - Server: RuntimeAppConfig (full)
   * - Client: RuntimeClientConfig (subset)
   *
   * Access this property after APP_INITIALIZER has run
   */
  config!: RuntimeClientConfig | RuntimeAppConfig;

  /**
   * Validate configuration using OpenChallenges server schema
   * This is called when loading from YAML files
   */
  protected override validateConfig(config: unknown): AppConfig {
    return validateConfig(config);
  }

  /**
   * Transform server config to client config before transferring to browser
   * This is called on the server before storing in TransferState
   */
  protected override transformForClient(serverConfig: AppConfig): RuntimeClientConfig {
    const clientConfig = transformServerToClientConfig(serverConfig);
    return {
      ...clientConfig,
      isPlatformServer: false, // Client is never running on server
    };
  }

  /**
   * Load configuration using the new YAML-based system
   * Called during APP_INITIALIZER phase
   */
  override async loadConfig(basePath?: string): Promise<RuntimeClientConfig | RuntimeAppConfig> {
    const loaded = await super.loadConfig(basePath);

    // Add isPlatformServer to the config if not already present
    if (isPlatformServer(this.platformId)) {
      // Server: Add isPlatformServer to full config
      this.config = {
        ...(loaded as AppConfig),
        isPlatformServer: true,
      } as RuntimeAppConfig;
    } else {
      // Client: Config already has isPlatformServer from transformation
      this.config = loaded as RuntimeClientConfig;
    }

    return this.config;
  }

  /**
   * Reload configuration
   * Useful for development or when config needs to be refreshed
   */
  override async reloadConfig(): Promise<RuntimeClientConfig | RuntimeAppConfig> {
    const reloaded = await super.reloadConfig();
    this.config = reloaded as RuntimeClientConfig | RuntimeAppConfig;
    return this.config;
  }

  /**
   * Get a specific config value by path
   * Example: getValue('api.baseUrl') returns config.api.baseUrl
   *
   * Note: Path syntax differs between server and client
   * - Server: 'api.baseUrls.ssr'
   * - Client: 'api.baseUrl'
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
