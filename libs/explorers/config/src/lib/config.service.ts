import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';
import { ConfigLoaderService } from '@sagebionetworks/platform/config/angular';
import { RuntimeAppConfig, validateConfig, AppConfig } from './config.schema';
import { RuntimeClientConfig } from './client-config.schema';
import { transformServerToClientConfig } from './config-transformer';

@Injectable({
  providedIn: 'root',
})
export class ConfigService extends ConfigLoaderService<
  RuntimeClientConfig | RuntimeAppConfig,
  AppConfig
> {
  protected override readonly platformId = inject(PLATFORM_ID);

  config!: RuntimeClientConfig | RuntimeAppConfig;

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

  override async loadConfig(basePath?: string): Promise<RuntimeClientConfig | RuntimeAppConfig> {
    const loaded = await super.loadConfig(basePath);

    if (isPlatformServer(this.platformId)) {
      this.config = {
        ...(loaded as AppConfig),
        isPlatformServer: true,
      } as RuntimeAppConfig;
    } else {
      this.config = loaded as RuntimeClientConfig;
    }

    return this.config;
  }
}
