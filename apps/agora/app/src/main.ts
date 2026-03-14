import { bootstrapApplication } from '@angular/platform-browser';
import { AppConfig } from '@sagebionetworks/agora/config';
import { initSentry } from '@sagebionetworks/explorers/sentry';
import { prefetchConfig } from '@sagebionetworks/explorers/util';

import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

prefetchConfig<AppConfig>().then((config) => {
  initSentry({
    dsn: 'https://3cfc84951936511803f5c86d82eb9cad@o4510881207418880.ingest.us.sentry.io/4510897622679552',
    hostEnvironmentMap: {
      'agora-dev.adknowledgeportal.org': 'dev',
      'agora-stage.adknowledgeportal.org': 'stage',
      'agora.adknowledgeportal.org': 'prod',
    },
    release: config?.sentryRelease,
  });

  bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err));
});
