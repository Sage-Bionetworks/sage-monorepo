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
    const loaded = await super.loadConfig(basePath);
    this.config = {
      ...(loaded as AppConfig),
      isPlatformServer: isPlatformServer(this.platformId),
    };
    return this.config;
  }
}
