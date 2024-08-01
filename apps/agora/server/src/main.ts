/**
 * This is not a production server yet!
 * This is only a minimal backend to get started.
 */

import express from 'express';
import path from 'path';
import api from './api';

const app = express();

app.use('/assets', express.static(path.join(__dirname, 'assets')));
app.use('/api', api);

// Health endpoint used by the container
app.get('/health', (_req, res) => res.status(200).json({ status: 'UP' }));

const port = process.env.PORT ?? 3333;
const server = app.listen(port, () => {
  console.log(`Listening at http://localhost:${port}/api`);
});
server.on('error', console.error);
