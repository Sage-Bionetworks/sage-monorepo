import { Injectable, inject, PLATFORM_ID, makeStateKey, TransferState } from '@angular/core';
import { isPlatformServer } from '@angular/common';
import { YamlParserService } from './yaml-parser.service';
import { EnvironmentMapperService } from './environment-mapper.service';
import { normalizeEnvironment } from './base-config.schema';

// State key for transferring config from server to browser
const CONFIG_STATE_KEY = makeStateKey<any>('app-config');

/**
 * Generic configuration loader service
 * Implements Spring Boot-style configuration hierarchy:
 * 1. Load base configuration (application.yaml)
 * 2. Load profile-specific configuration (application-{profile}.yaml)
 * 3. Apply environment variable overrides
 * 4. Validate final configuration
 * 5. Transfer state from server to browser (SSR)
 *
 * This is a generic service that can be extended for specific applications
 */
@Injectable()
export abstract class ConfigLoaderService<T> {
  protected readonly yamlParser = inject(YamlParserService);
  protected readonly envMapper = inject(EnvironmentMapperService);
  protected readonly platformId = inject(PLATFORM_ID);
  protected readonly transferState = inject(TransferState);

  protected configCache: T | null = null;

  /**
   * Abstract method to validate configuration
   * Must be implemented by subclasses with their specific schema
   */
  protected abstract validateConfig(config: unknown): T;

  /**
   * Check if running on server (Node.js)
   */
  protected get isServer(): boolean {
    return isPlatformServer(this.platformId);
  }

  /**
   * Get the active profile name for loading profile-specific config file
   * Priority: ENVIRONMENT > NODE_ENV > environment from application.yaml
   *
   * Uses normalizeEnvironment() to map aliases to canonical values:
   * - development → dev
   * - staging → stage
   * - production → prod
   *
   * Examples:
   * - ENVIRONMENT=stage → loads application-stage.yaml
   * - ENVIRONMENT=prod → loads application-prod.yaml
   * - NODE_ENV=development → loads application-dev.yaml (normalized)
   * - No env vars → reads 'environment' from application.yaml
   *
   * Note: Default values always come from application.yaml, not from the profile
   */
  private getActiveProfile(baseConfig?: Record<string, any>): string {
    if (this.isServer) {
      const rawProfile =
        process.env['ENVIRONMENT'] ||
        process.env['NODE_ENV'] ||
        baseConfig?.['environment'] ||
        'development';
      return normalizeEnvironment(rawProfile as any);
    }
    // For browser, use environment from base config or default to 'dev'
    const browserEnv = baseConfig?.['environment'] || 'dev';
    return normalizeEnvironment(browserEnv as any);
  }

  /**
   * Load complete application configuration
   * Merges all configuration sources and applies overrides
   *
   * In SSR mode:
   * - Server: Loads from YAML files with env var overrides, stores in TransferState
   * - Browser: Retrieves from TransferState (same config as server)
   *
   * This ensures 12-factor compliance: browser uses the same environment-specific
   * configuration as the server, including all environment variable overrides.
   *
   * @param basePath - Optional base path for config files (for testing)
   * @returns Complete validated configuration
   */
  async loadConfig(basePath?: string): Promise<T> {
    // Return cached config if available
    if (this.configCache) {
      return this.configCache;
    }

    try {
      // In browser context with SSR, check if config was transferred from server
      if (!this.isServer) {
        const transferredConfig = this.transferState.get(CONFIG_STATE_KEY, null);
        if (transferredConfig) {
          console.log('[ConfigLoader] Using configuration transferred from server');
          console.log('[ConfigLoader] Browser configuration (from server via TransferState):');
          console.log(JSON.stringify(transferredConfig, null, 2));
          const validatedConfig = this.validateConfig(transferredConfig);
          this.configCache = validatedConfig;
          return validatedConfig;
        }
        console.log('[ConfigLoader] No transferred state found, loading from YAML files');
      }

      // Server or browser without SSR: Load from YAML files
      // 1. Load base configuration first to determine default environment
      const baseConfig = await this.yamlParser.loadYaml('application.yaml', basePath);
      if (!baseConfig) {
        throw new Error('Base configuration file (application.yaml) not found');
      }

      // 2. Determine active profile using base config as fallback
      const profile = this.getActiveProfile(baseConfig);
      console.log(`[ConfigLoader] Loading configuration for profile: ${profile}`);

      let mergedConfig = { ...baseConfig };

      // 3. Load profile-specific configuration
      const profileConfig = await this.yamlParser.loadYaml(`application-${profile}.yaml`, basePath);
      if (profileConfig) {
        console.log(`[ConfigLoader] Merging profile configuration: application-${profile}.yaml`);
        mergedConfig = this.yamlParser.deepMerge(mergedConfig, profileConfig);
      }

      // 4. Apply environment variable overrides (server only)
      if (this.isServer) {
        console.log('[ConfigLoader] Applying environment variable overrides');
        mergedConfig = this.envMapper.applyEnvironmentOverrides(mergedConfig);

        // Log which environment variables were applied (for debugging)
        const appliedEnvVars = this.envMapper.getMatchingEnvironmentVariables(mergedConfig);
        if (Object.keys(appliedEnvVars).length > 0) {
          console.log('[ConfigLoader] Applied environment variables:', appliedEnvVars);
        }
      }

      // 5. Validate configuration using subclass implementation
      console.log('[ConfigLoader] Validating configuration');
      const validatedConfig = this.validateConfig(mergedConfig);

      // 6. On server, store config in TransferState for browser hydration
      if (this.isServer) {
        console.log('[ConfigLoader] Storing configuration in TransferState for browser');
        console.log('[ConfigLoader] Server configuration (will be transferred to browser):');
        console.log(JSON.stringify(mergedConfig, null, 2));
        this.transferState.set(CONFIG_STATE_KEY, mergedConfig);
      } else {
        // Browser without SSR
        console.log('[ConfigLoader] Browser configuration (loaded from YAML, no SSR):');
        console.log(JSON.stringify(mergedConfig, null, 2));
      }

      // Cache the configuration
      this.configCache = validatedConfig;

      console.log('[ConfigLoader] Configuration loaded successfully');
      return validatedConfig;
    } catch (error) {
      console.error('[ConfigLoader] Failed to load configuration:', error);
      throw new Error(`Configuration loading failed: ${error}`);
    }
  }

  /**
   * Get cached configuration
   * Throws error if config hasn't been loaded yet
   */
  getConfig(): T {
    if (!this.configCache) {
      throw new Error('Configuration not loaded. Call loadConfig() first.');
    }
    return this.configCache;
  }

  /**
   * Clear cached configuration
   * Useful for testing or hot reload scenarios
   */
  clearCache(): void {
    this.configCache = null;
  }

  /**
   * Reload configuration
   * Clears cache and loads configuration again
   */
  async reloadConfig(basePath?: string): Promise<T> {
    this.clearCache();
    return this.loadConfig(basePath);
  }

  /**
   * Set configuration directly (for testing)
   * Bypasses normal loading process
   */
  setConfig(config: T): void {
    this.configCache = config;
  }
}
