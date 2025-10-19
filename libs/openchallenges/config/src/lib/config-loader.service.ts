import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';
import { YamlParserService } from './yaml-parser.service';
import { EnvironmentMapperService } from './environment-mapper.service';
import { RuntimeAppConfig, validateConfig } from './config.schema';

/**
 * Service for loading application configuration
 * Implements Spring Boot-style configuration hierarchy:
 * 1. Load base configuration (application.yaml)
 * 2. Load profile-specific configuration (application-{profile}.yaml)
 * 3. Load local overrides (application-local.yaml) - optional
 * 4. Apply environment variable overrides
 * 5. Validate final configuration
 */
@Injectable({
  providedIn: 'root',
})
export class ConfigLoaderService {
  private readonly yamlParser = inject(YamlParserService);
  private readonly envMapper = inject(EnvironmentMapperService);
  private readonly platformId = inject(PLATFORM_ID);

  private configCache: RuntimeAppConfig | null = null;

  /**
   * Check if running on server (Node.js)
   */
  private get isServer(): boolean {
    return isPlatformServer(this.platformId);
  }

  /**
   * Map standard NODE_ENV values to config file names
   * NODE_ENV is set automatically by build tools (development, production, test)
   * For custom environments like 'stage', use ACTIVE_PROFILE instead
   */
  private readonly profileFileMap: Record<string, string> = {
    development: 'dev',
    production: 'prod',
  };

  /**
   * Get the active profile name for loading profile-specific config file
   * Priority: ACTIVE_PROFILE > NODE_ENV > 'development' (which maps to 'dev' file)
   *
   * Use ACTIVE_PROFILE for custom environments (e.g., ACTIVE_PROFILE=stage)
   * NODE_ENV is typically set by build tools (development, production, test)
   *
   * Note: Default values always come from application.yaml, not from the profile
   */
  private getActiveProfile(): string {
    if (this.isServer) {
      const rawProfile = process.env['ACTIVE_PROFILE'] || process.env['NODE_ENV'] || 'development';
      return this.profileFileMap[rawProfile] || rawProfile;
    }
    return 'dev'; // Default to 'dev' profile for browser
  }

  /**
   * Load complete application configuration
   * Merges all configuration sources and applies overrides
   *
   * @param basePath - Optional base path for config files (for testing)
   * @returns Complete validated configuration
   */
  async loadConfig(basePath?: string): Promise<RuntimeAppConfig> {
    // Return cached config if available
    if (this.configCache) {
      return this.configCache;
    }

    try {
      const profile = this.getActiveProfile();
      console.log(`[ConfigLoader] Loading configuration for profile: ${profile}`);

      // 1. Load base configuration
      const baseConfig = await this.yamlParser.loadYaml('application.yaml', basePath);
      if (!baseConfig) {
        throw new Error('Base configuration file (application.yaml) not found');
      }

      let mergedConfig = { ...baseConfig };

      // 2. Load profile-specific configuration
      const profileConfig = await this.yamlParser.loadYaml(`application-${profile}.yaml`, basePath);
      if (profileConfig) {
        console.log(`[ConfigLoader] Merging profile configuration: application-${profile}.yaml`);
        mergedConfig = this.yamlParser.deepMerge(mergedConfig, profileConfig);
      }

      // 3. Apply environment variable overrides
      // Environment variables are loaded from .env files by Nx and available in process.env
      if (this.isServer) {
        console.log('[ConfigLoader] Applying environment variable overrides');
        mergedConfig = this.envMapper.applyEnvironmentOverrides(mergedConfig);

        // Log which environment variables were applied (for debugging)
        const appliedEnvVars = this.envMapper.getMatchingEnvironmentVariables(mergedConfig);
        if (Object.keys(appliedEnvVars).length > 0) {
          console.log('[ConfigLoader] Applied environment variables:', appliedEnvVars);
        }
      }

      // 4. Validate configuration
      console.log('[ConfigLoader] Validating configuration');
      const validatedConfig = validateConfig(mergedConfig);

      // 5. Add runtime properties
      const runtimeConfig: RuntimeAppConfig = {
        ...validatedConfig,
        isPlatformServer: this.isServer,
      };

      // Cache the configuration
      this.configCache = runtimeConfig;

      console.log('[ConfigLoader] Configuration loaded successfully');
      return runtimeConfig;
    } catch (error) {
      console.error('[ConfigLoader] Failed to load configuration:', error);
      throw new Error(`Configuration loading failed: ${error}`);
    }
  }

  /**
   * Get cached configuration
   * Throws error if config hasn't been loaded yet
   */
  getConfig(): RuntimeAppConfig {
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
  async reloadConfig(basePath?: string): Promise<RuntimeAppConfig> {
    this.clearCache();
    return this.loadConfig(basePath);
  }

  /**
   * Set configuration directly (for testing)
   * Bypasses normal loading process
   */
  setConfig(config: RuntimeAppConfig): void {
    this.configCache = config;
  }
}
