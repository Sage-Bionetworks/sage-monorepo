/***************************************************************************************************
 * Initialize the server environment - for example, adding DOM built-in types to the global scope.
 *
 * NOTE:
 * This import must come before any imports (direct or transitive) that rely on DOM built-ins being
 * available, such as `@angular/elements`.
 */
import '@angular/platform-server/init';

import { enableProdMode } from '@angular/core';

// import { environment } from './environments/environment';

// if (environment.production) {
//   enableProdMode();
// }

import {
  AppConfig,
  Environment,
} from '@sagebionetworks/challenge-registry/config';

fetch('/config/config.json')
  .then((response) => response.json() as Promise<AppConfig>)
  .then((config: AppConfig) => {
    if (
      [Environment.Production, Environment.Staging].includes(config.environment)
    ) {
      enableProdMode();
    }
  });

export { AppServerModule } from './app/app.server.module';
export { renderModule } from '@angular/platform-server';
