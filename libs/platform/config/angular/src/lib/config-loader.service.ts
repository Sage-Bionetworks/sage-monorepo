import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';
import { YamlParserService } from './yaml-parser.service';
import { EnvironmentMapperService } from './environment-mapper.service';

/**
 * Generic configuration loader service
 * Implements Spring Boot-style configuration hierarchy:
 * 1. Load base configuration (application.yaml)
 * 2. Load profile-specific configuration (application-{profile}.yaml)
 * 3. Apply environment variable overrides
 * 4. Validate final configuration
 *
 * This is a generic service that can be extended for specific applications
 */
@Injectable()
export abstract class ConfigLoaderService<T> {
  protected readonly yamlParser = inject(YamlParserService);
  protected readonly envMapper = inject(EnvironmentMapperService);
  protected readonly platformId = inject(PLATFORM_ID);

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
   * Map standard NODE_ENV values to config file names
   * NODE_ENV is set automatically by build tools (development, production, test)
   */
  private readonly profileFileMap: Record<string, string> = {
    development: 'dev',
    production: 'prod',
  };

  /**
   * Get the active profile name for loading profile-specific config file
   * Priority: ENVIRONMENT > NODE_ENV > 'development' (which maps to 'dev' file)
   *
   * ENVIRONMENT should match the 'environment' property in your config schema
   * NODE_ENV is typically set by build tools (development, production, test)
   *
   * Examples:
   * - ENVIRONMENT=stage → loads application-stage.yaml
   * - ENVIRONMENT=prod → loads application-prod.yaml
   * - NODE_ENV=development → loads application-dev.yaml (mapped)
   *
   * Note: Default values always come from application.yaml, not from the profile
   */
  private getActiveProfile(): string {
    if (this.isServer) {
      const rawProfile = process.env['ENVIRONMENT'] || process.env['NODE_ENV'] || 'development';
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
  async loadConfig(basePath?: string): Promise<T> {
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

      // 4. Validate configuration using subclass implementation
      console.log('[ConfigLoader] Validating configuration');
      const validatedConfig = this.validateConfig(mergedConfig);

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
