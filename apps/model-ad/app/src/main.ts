import { bootstrapApplication } from '@angular/platform-browser';
import * as Sentry from '@sentry/angular';

import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

function getSentryEnvironment(): string {
  if (typeof window === 'undefined') return 'server';

  const hostname = window.location.hostname;
  if (hostname === 'localhost' || hostname === '127.0.0.1') return 'localhost';
  if (hostname === 'dev.modeladexplorer.org') return 'dev';
  if (hostname === 'stage.modeladexplorer.org') return 'stage';
  if (hostname === 'modeladexplorer.org') return 'production';

  return 'localhost';
}

Sentry.init({
  dsn: 'https://bbf0d51ab53013fe73d95348a6bffe61@o4510881207418880.ingest.us.sentry.io/4510896864559104',
  environment: getSentryEnvironment(),
  sendDefaultPii: true,
});

bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err));
