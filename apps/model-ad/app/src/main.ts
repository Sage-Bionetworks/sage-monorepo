import { bootstrapApplication } from '@angular/platform-browser';
import { initSentry } from '@sagebionetworks/explorers/sentry';
import { prefetchConfig } from '@sagebionetworks/explorers/util';

import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

(async () => {
  const config = await prefetchConfig<{ sentryRelease?: string }>();

  initSentry({
    dsn: 'https://bbf0d51ab53013fe73d95348a6bffe61@o4510881207418880.ingest.us.sentry.io/4510896864559104',
    hostEnvironmentMap: {
      'dev.modeladexplorer.org': 'dev',
      'stage.modeladexplorer.org': 'stage',
      'modeladexplorer.org': 'prod',
    },
    release: config?.sentryRelease,
  });

  bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err));
})();
