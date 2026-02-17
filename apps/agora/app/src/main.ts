import { bootstrapApplication } from '@angular/platform-browser';
import * as Sentry from '@sentry/angular';

import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { SENTRY_RELEASE } from './sentry-release';

function getSentryEnvironment(): string {
  if (typeof window === 'undefined') return 'server';

  const hostname = window.location.hostname;
  if (hostname === 'localhost' || hostname === '127.0.0.1') return 'localhost';
  if (hostname === 'agora-dev.adknowledgeportal.org') return 'dev';
  if (hostname === 'agora-stage.adknowledgeportal.org') return 'stage';
  if (hostname === 'agora.adknowledgeportal.org') return 'production';

  return 'staging';
}

Sentry.init({
  dsn: 'https://cbf3a9c7156b68be506256fc3a346413@o4510881207418880.ingest.us.sentry.io/4510881239597056',
  environment: getSentryEnvironment(),
  release: SENTRY_RELEASE,
  sendDefaultPii: true,
});

bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err));
