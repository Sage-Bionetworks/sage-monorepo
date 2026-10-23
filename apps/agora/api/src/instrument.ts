import { initNodeSentry } from '@sagebionetworks/explorers/sentry/node';

initNodeSentry({
  dsn: process.env.SENTRY_DSN ?? '',
  environment: process.env.SENTRY_ENVIRONMENT ?? '',
  release: process.env.SENTRY_RELEASE,
});
