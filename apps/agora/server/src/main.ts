/**
 * This is not a production server yet!
 * This is only a minimal backend to get started.
 */

import express from 'express';
import * as path from 'path';
import api from './api';

const app = express();

app.use('/assets', express.static(path.join(__dirname, 'assets')));
app.use('/api', api);

const port = process.env.PORT ?? 3333;
const server = app.listen(port, () => {
  console.log(`Listening at http://localhost:${port}/api`);
});
server.on('error', console.error);
