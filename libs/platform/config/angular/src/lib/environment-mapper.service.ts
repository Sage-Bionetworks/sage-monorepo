import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';

/**
 * Service for mapping environment variables to configuration properties
 * Follows Spring Boot convention: foo.bar.baz → FOO_BAR_BAZ
 * Also supports camelCase: csrApiUrl → CSR_API_URL
 */
@Injectable({
  providedIn: 'root',
})
export class EnvironmentMapperService {
  private readonly platformId = inject(PLATFORM_ID);

  /**
   * Check if running on server (Node.js) where process.env is available
   */
  private get isServer(): boolean {
    return isPlatformServer(this.platformId);
  }

  /**
   * Apply environment variable overrides to configuration object
   * @param config - Base configuration object
   * @returns Configuration with environment variable overrides applied
   */
  applyEnvironmentOverrides<T extends Record<string, any>>(config: T): T {
    if (!this.isServer) {
      // Environment variables are not available in browser context
      return config;
    }

    const result = { ...config };
    this.applyOverridesRecursive(result, []);
    return result;
  }

  /**
   * Recursively traverse config object and apply environment variable overrides
   * @param obj - Current object being traversed
   * @param path - Current path in dot notation
   */
  private applyOverridesRecursive(obj: any, path: string[]): void {
    for (const key in obj) {
      if (!Object.prototype.hasOwnProperty.call(obj, key)) {
        continue;
      }

      const currentPath = [...path, key];
      const value = obj[key];

      if (value && typeof value === 'object' && !Array.isArray(value)) {
        // Recursively process nested objects
        this.applyOverridesRecursive(value, currentPath);
      } else {
        // Check for environment variable override
        const envValue = this.getEnvironmentValue(currentPath);
        if (envValue !== undefined) {
          obj[key] = envValue;
        }
      }
    }
  }

  /**
   * Get environment variable value for a given path
   * Tries multiple naming conventions:
   * 1. SCREAMING_SNAKE_CASE with underscores: FOO_BAR_BAZ
   * 2. SCREAMING_SNAKE_CASE with double underscores: FOO__BAR__BAZ
   *
   * @param path - Property path as array (e.g., ['api', 'csr', 'url'])
   * @returns Parsed environment variable value or undefined
   */
  private getEnvironmentValue(path: string[]): any {
    const envVarNames = [
      // Convert to SCREAMING_SNAKE_CASE with single underscores
      path.map((segment) => this.toScreamingSnakeCase(segment)).join('_'),
      // Convert to SCREAMING_SNAKE_CASE with double underscores
      path.map((segment) => this.toScreamingSnakeCase(segment)).join('__'),
    ];

    for (const envVarName of envVarNames) {
      const envValue = process.env[envVarName];
      if (envValue !== undefined) {
        return this.parseEnvironmentValue(envValue);
      }
    }

    return undefined;
  }

  /**
   * Convert camelCase or PascalCase to SCREAMING_SNAKE_CASE
   * Examples:
   * - csrApiUrl → CSR_API_URL
   * - googleTagManagerId → GOOGLE_TAG_MANAGER_ID
   * - apiUrl → API_URL
   */
  private toScreamingSnakeCase(str: string): string {
    return str
      .replace(/([a-z])([A-Z])/g, '$1_$2') // Insert underscore between lowercase and uppercase
      .replace(/([A-Z])([A-Z][a-z])/g, '$1_$2') // Handle consecutive uppercase letters
      .toUpperCase();
  }

  /**
   * Parse environment variable string value to appropriate type
   * Handles: boolean, number, JSON objects/arrays, strings
   */
  private parseEnvironmentValue(value: string): any {
    // Handle boolean values
    if (value.toLowerCase() === 'true') {
      return true;
    }
    if (value.toLowerCase() === 'false') {
      return false;
    }

    // Handle null/undefined
    if (value.toLowerCase() === 'null' || value.toLowerCase() === 'undefined') {
      return null;
    }

    // Handle numbers
    if (/^-?\d+\.?\d*$/.test(value)) {
      const parsed = Number(value);
      if (!isNaN(parsed)) {
        return parsed;
      }
    }

    // Try to parse as JSON (for objects/arrays)
    if (value.startsWith('{') || value.startsWith('[')) {
      try {
        return JSON.parse(value);
      } catch {
        // If JSON parsing fails, treat as string
      }
    }

    // Return as string
    return value;
  }

  /**
   * Get all environment variables that match configuration keys
   * Useful for debugging
   */
  getMatchingEnvironmentVariables(config: Record<string, any>): Record<string, any> {
    if (!this.isServer) {
      return {};
    }

    const matches: Record<string, any> = {};
    this.collectMatchingEnvVars(config, [], matches);
    return matches;
  }

  /**
   * Recursively collect matching environment variables
   */
  private collectMatchingEnvVars(obj: any, path: string[], matches: Record<string, any>): void {
    for (const key in obj) {
      if (!Object.prototype.hasOwnProperty.call(obj, key)) {
        continue;
      }

      const currentPath = [...path, key];
      const value = obj[key];

      if (value && typeof value === 'object' && !Array.isArray(value)) {
        this.collectMatchingEnvVars(value, currentPath, matches);
      } else {
        const envValue = this.getEnvironmentValue(currentPath);
        if (envValue !== undefined) {
          const pathString = currentPath.join('.');
          matches[pathString] = envValue;
        }
      }
    }
  }
}
