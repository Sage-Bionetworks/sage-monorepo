import { bootstrapApplication } from '@angular/platform-browser';
import { getSentryEnvironment } from '@sagebionetworks/explorers/sentry';
import * as Sentry from '@sentry/angular';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

Sentry.init({
  dsn: 'https://bbf0d51ab53013fe73d95348a6bffe61@o4510881207418880.ingest.us.sentry.io/4510896864559104',
  environment: getSentryEnvironment({
    'dev.modeladexplorer.org': 'dev',
    'stage.modeladexplorer.org': 'stage',
    'modeladexplorer.org': 'prod',
  }),
});

bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err));
