import { bootstrapApplication } from '@angular/platform-browser';
import { AppConfig } from '@sagebionetworks/model-ad/config';
import { initSentry } from '@sagebionetworks/explorers/sentry';
import { prefetchConfig } from '@sagebionetworks/explorers/util';

import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

prefetchConfig<AppConfig>().then((config) => {
  initSentry({
    dsn: config?.sentryDSN,
    environment: config?.sentryEnvironment,
    release: config?.sentryRelease,
  });

  bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err));
});
