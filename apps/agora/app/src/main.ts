import { bootstrapApplication } from '@angular/platform-browser';
import * as Sentry from '@sentry/angular';

import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

function getSentryEnvironment(): string {
  if (typeof window === 'undefined') return 'server';

  const hostname = window.location.hostname;
  if (hostname === 'localhost' || hostname === '127.0.0.1') return 'localhost';
  if (hostname === 'agora-dev.adknowledgeportal.org') return 'dev';
  if (hostname === 'agora-stage.adknowledgeportal.org') return 'stage';
  if (hostname === 'agora.adknowledgeportal.org') return 'production';

  return 'localhost';
}

Sentry.init({
  dsn: 'https://3cfc84951936511803f5c86d82eb9cad@o4510881207418880.ingest.us.sentry.io/4510897622679552',
  environment: getSentryEnvironment(),
  sendDefaultPii: true,
});

bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err));
