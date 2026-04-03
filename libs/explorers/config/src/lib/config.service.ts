import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';
import { ConfigLoaderService } from '@sagebionetworks/platform/config/angular';
import { RuntimeServerConfig, validateConfig, AppConfig } from './config.schema';
import { RuntimeClientConfig } from './client-config.schema';
import { transformServerToClientConfig } from './config-transformer';

@Injectable({
  providedIn: 'root',
})
export class ConfigService extends ConfigLoaderService<
  RuntimeClientConfig | RuntimeServerConfig,
  AppConfig
> {
  protected override readonly platformId = inject(PLATFORM_ID);

  config!: RuntimeClientConfig | RuntimeServerConfig;

  protected override validateConfig(config: unknown): AppConfig {
    return validateConfig(config);
  }

  protected override transformForClient(serverConfig: AppConfig): RuntimeClientConfig {
    const clientConfig = transformServerToClientConfig(serverConfig);
    return {
      ...clientConfig,
      isPlatformServer: false,
    };
  }

  override async loadConfig(basePath?: string): Promise<RuntimeClientConfig | RuntimeServerConfig> {
    const loaded = await super.loadConfig(basePath);

    if (isPlatformServer(this.platformId)) {
      this.config = {
        ...(loaded as AppConfig),
        isPlatformServer: true,
      } as RuntimeServerConfig;
    } else {
      this.config = loaded as RuntimeClientConfig;
    }

    return this.config;
  }
}
