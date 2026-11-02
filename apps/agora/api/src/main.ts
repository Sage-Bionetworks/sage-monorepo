/**
 * This is not a production server yet!
 * This is only a minimal backend to get started.
 */
import './instrument';

import '@angular/compiler';
import '@angular/platform-browser-dynamic';

import { sentryExpressErrorHandler } from '@sagebionetworks/explorers/sentry/node';
import compression from 'compression';
import express from 'express';
import path from 'path';
import api from './api';
import { dataReleaseContext, dataReleaseTracker } from './data-release';

const app = express();
app.use(compression());

app.use('/assets', express.static(path.join(__dirname, 'assets')));
app.use('/v1', dataReleaseContext(dataReleaseTracker), api);

// Health endpoint used by the container. The data release state never changes the status, so a
// database outage can't get every API task replaced at once.
app.get('/health', (_req, res) =>
  res.status(200).json({ status: 'UP', dataRelease: dataReleaseTracker.getState() }),
);

app.use(sentryExpressErrorHandler());

const port = process.env.PORT ?? 3333;
const server = app.listen(port, () => {
  console.log(`Listening at http://localhost:${port}`);
});
server.on('error', console.error);
