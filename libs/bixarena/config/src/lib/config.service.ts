import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';
import { ConfigLoaderService } from '@sagebionetworks/platform/config/angular';
import { RuntimeAppConfig, validateConfig, AppConfig } from './config.schema';

@Injectable({
  providedIn: 'root',
})
export class ConfigService extends ConfigLoaderService<RuntimeAppConfig, AppConfig> {
  protected override readonly platformId = inject(PLATFORM_ID);

  config!: RuntimeAppConfig;

  protected override validateConfig(config: unknown): AppConfig {
    return validateConfig(config);
  }

  override async loadConfig(basePath?: string): Promise<RuntimeAppConfig> {
    // On the server, resolve the config path relative to the workspace root.
    // YamlParserService first tries ../../browser/config (production built path).
    // If that fails it falls back to a hardcoded OC path — override it here for BixArena.
    let resolvedPath = basePath;
    if (!resolvedPath && isPlatformServer(this.platformId)) {
      const dynamicImport = new Function('specifier', 'return import(specifier)');
      const { resolve } = await dynamicImport('node:path');
      resolvedPath = resolve(process.cwd(), 'apps/bixarena/app-angular/src/config');
    }
    const loaded = await super.loadConfig(resolvedPath);
    this.config = {
      ...(loaded as AppConfig),
      isPlatformServer: isPlatformServer(this.platformId),
    };
    return this.config;
  }
}
