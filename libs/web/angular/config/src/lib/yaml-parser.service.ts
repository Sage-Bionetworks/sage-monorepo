import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom } from 'rxjs';
import * as yaml from 'js-yaml';

/**
 * Service for loading and parsing YAML configuration files
 * Works in both browser (via HTTP) and Node.js (via fs) contexts
 */
@Injectable({
  providedIn: 'root',
})
export class YamlParserService {
  private readonly http = inject(HttpClient);
  private readonly platformId = inject(PLATFORM_ID);

  /**
   * Check if running on server (Node.js) or browser
   */
  private get isServer(): boolean {
    return isPlatformServer(this.platformId);
  }

  /**
   * Load and parse a YAML file
   * @param filename - Name of the YAML file (e.g., 'application.yaml')
   * @param basePath - Base path for the config files
   * @returns Parsed YAML as JavaScript object
   */
  async loadYaml(filename: string, basePath?: string): Promise<Record<string, any> | null> {
    try {
      if (this.isServer) {
        console.log(`[YamlParser] Loading ${filename} from server (Node.js)`);
        return await this.loadYamlServer(filename, basePath);
      } else {
        console.log(`[YamlParser] Loading ${filename} from browser (HTTP)`);
        return await this.loadYamlBrowser(filename, basePath);
      }
    } catch (error) {
      // File might not exist (e.g., application-local.yaml)
      // This is not necessarily an error
      console.debug(`Could not load ${filename}:`, error);
      return null;
    }
  }

  /**
   * Load YAML file in server context (Node.js)
   */
  private async loadYamlServer(filename: string, basePath?: string): Promise<Record<string, any>> {
    // Use eval to prevent bundler from trying to resolve Node.js modules
    // This ensures Node.js modules are only loaded at runtime on the server
    const dynamicImport = new Function('specifier', 'return import(specifier)');

    const { readFile, access } = await dynamicImport('node:fs/promises');
    const { join, dirname, resolve } = await dynamicImport('node:path');
    const { fileURLToPath } = await dynamicImport('node:url');
    const { constants } = await dynamicImport('node:fs');

    // Determine the config directory path
    let configPath: string | undefined;
    if (basePath) {
      configPath = basePath;
    } else {
      const currentDir = dirname(fileURLToPath(import.meta.url));

      // Try multiple possible paths (development and production)
      const possiblePaths = [
        // Production: dist/apps/openchallenges/app/browser/config
        join(currentDir, '../../browser/config'),
        // Development with Vite/ng serve: Check relative to project root
        resolve(process.cwd(), 'apps/openchallenges/app/src/config'),
        // Alternative production path
        join(currentDir, '../browser/config'),
      ];

      // Find the first path that exists
      for (const path of possiblePaths) {
        try {
          await access(path, constants.R_OK);
          configPath = path;
          console.log(`[YamlParser] Found config directory: ${configPath}`);
          break;
        } catch {
          // Path doesn't exist, try next
          continue;
        }
      }

      if (!configPath) {
        throw new Error(
          `Could not locate config directory. Tried paths: ${possiblePaths.join(', ')}`,
        );
      }
    }

    const filePath = join(configPath, filename);
    console.log(`[YamlParser] Reading file: ${filePath}`);
    const fileContent = await readFile(filePath, 'utf-8');
    const parsed = yaml.load(fileContent);

    if (typeof parsed !== 'object' || parsed === null) {
      throw new Error(`Invalid YAML content in ${filename}`);
    }

    return parsed as Record<string, any>;
  }

  /**
   * Load YAML file in browser context (via HTTP)
   */
  private async loadYamlBrowser(filename: string, basePath?: string): Promise<Record<string, any>> {
    const configUrl = basePath ? `${basePath}/${filename}` : `/config/${filename}`;
    const yamlContent = await lastValueFrom(this.http.get(configUrl, { responseType: 'text' }));
    const parsed = yaml.load(yamlContent);

    if (typeof parsed !== 'object' || parsed === null) {
      throw new Error(`Invalid YAML content in ${filename}`);
    }

    return parsed as Record<string, any>;
  }

  /**
   * Parse YAML string content
   * @param yamlContent - YAML content as string
   * @returns Parsed YAML as JavaScript object
   */
  parseYaml(yamlContent: string): Record<string, any> {
    const parsed = yaml.load(yamlContent);

    if (typeof parsed !== 'object' || parsed === null) {
      throw new Error('Invalid YAML content');
    }

    return parsed as Record<string, any>;
  }

  /**
   * Deeply merge two objects
   * Properties from the source object override those in the target
   * @param target - Target object (lower priority)
   * @param source - Source object (higher priority)
   * @returns Merged object
   */
  deepMerge<T extends Record<string, any>>(target: T, source: Partial<T>): T {
    const result = { ...target };

    for (const key in source) {
      if (Object.prototype.hasOwnProperty.call(source, key)) {
        const sourceValue = source[key];
        const targetValue = result[key];

        if (
          sourceValue &&
          typeof sourceValue === 'object' &&
          !Array.isArray(sourceValue) &&
          targetValue &&
          typeof targetValue === 'object' &&
          !Array.isArray(targetValue)
        ) {
          // Recursively merge nested objects
          result[key] = this.deepMerge(targetValue, sourceValue);
        } else {
          // Override with source value
          result[key] = sourceValue as any;
        }
      }
    }

    return result;
  }
}
