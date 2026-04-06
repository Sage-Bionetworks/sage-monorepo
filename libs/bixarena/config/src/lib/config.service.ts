import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { ConfigLoaderService } from '@sagebionetworks/platform/config/angular';
import { RuntimeServerConfig, validateConfig, AppConfig } from './config.schema';

@Injectable({
  providedIn: 'root',
})
export class ConfigService extends ConfigLoaderService<RuntimeServerConfig, AppConfig> {
  protected override readonly platformId = inject(PLATFORM_ID);

  config!: RuntimeServerConfig;

  protected override validateConfig(config: unknown): AppConfig {
    return validateConfig(config);
  }

  override async loadConfig(basePath?: string): Promise<RuntimeServerConfig> {
    const loaded = await super.loadConfig(basePath);
    this.config = {
      ...(loaded as AppConfig),
      isPlatformServer: true,
    } as RuntimeServerConfig;
    return this.config;
  }
}
