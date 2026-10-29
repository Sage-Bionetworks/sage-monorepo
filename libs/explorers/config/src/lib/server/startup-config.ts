import { HttpClient } from '@angular/common/http';
import { APP_ID, Injector, PLATFORM_ID, TransferState } from '@angular/core';
import {
  EnvironmentMapperService,
  YamlParserService,
} from '@sagebionetworks/platform/config/angular';
import { ConfigService } from '../config.service';

const STARTUP_APP_ID = 'startup-config-check';

/**
 * Loads and validates the app config once, through the same `ConfigService` every server-side
 * render uses, and rejects if it is invalid. Run it before the server listens: each render loads
 * the config itself, so an invalid value, such as a mistyped environment variable, would otherwise
 * fail every page while the health endpoint still reports the server as up.
 *
 * `configDirectory` is the directory the build copies the YAML config to, `<browser dist>/config`.
 */
export async function assertValidAppConfig(configDirectory: string): Promise<void> {
  const injector = Injector.create({
    providers: [
      { provide: PLATFORM_ID, useValue: 'server' },
      { provide: APP_ID, useValue: STARTUP_APP_ID },
      // The YAML loader reads files from disk on the server and only uses HTTP in the browser.
      { provide: HttpClient, useValue: {} },
      { provide: TransferState, useClass: TransferState },
      { provide: YamlParserService, useClass: YamlParserService },
      { provide: EnvironmentMapperService, useClass: EnvironmentMapperService },
      { provide: ConfigService, useClass: ConfigService },
    ],
  });
  await injector.get(ConfigService).loadConfig(configDirectory);
}
