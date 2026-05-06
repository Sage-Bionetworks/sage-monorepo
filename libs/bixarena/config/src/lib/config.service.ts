import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';
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
    // TODO: remove once the shared platform/config lib routes its logs through the
    // shared Logger token (with isDevMode-gated default). This is a bixarena-only
    // workaround to keep [ConfigLoader] and [YamlParser] noise out of the console.
    const originalLog = console.log;
    const originalDebug = console.debug;
    console.log = () => undefined;
    console.debug = () => undefined;
    try {
      const loaded = await super.loadConfig(basePath);
      this.config = {
        ...(loaded as AppConfig),
        isPlatformServer: isPlatformServer(this.platformId),
      } as RuntimeServerConfig;
      return this.config;
    } finally {
      console.log = originalLog;
      console.debug = originalDebug;
    }
  }
}
