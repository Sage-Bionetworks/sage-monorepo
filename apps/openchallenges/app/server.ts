import 'zone.js/dist/zone-node';

import { APP_BASE_HREF } from '@angular/common';
import { ngExpressEngine } from '@nguniversal/express-engine';
import * as express from 'express';
import { existsSync } from 'fs';
import { join } from 'path';

import bootstrap from './src/main.server';

// that process port comes from the above builder utils
// About setting a constant value: https://github.com/angular/universal/issues/1628
const PORT = process.env['PORT'] || '4200';
console.log(`server.ts: ${PORT}`);

// The Express app is exported so that it can be used by serverless Functions.
export function app(): express.Express {
  const server = express();
  const distFolder = join(
    process.cwd(),
    'dist/apps/openchallenges/app/browser/browser'
  );
  const indexHtml = existsSync(join(distFolder, 'index.original.html'))
    ? 'index.original.html'
    : 'index';

  // Our Universal express-engine (found @ https://github.com/angular/universal/tree/main/modules/express-engine)
  server.engine(
    'html',
    ngExpressEngine({
      bootstrap,
    })
  );

  server.set('view engine', 'html');
  server.set('views', distFolder);

  // Example Express Rest API endpoints
  // server.get('/api/**', (req, res) => { });
  // Serve static files from /browser
  server.get(
    '*.*',
    express.static(distFolder, {
      maxAge: '1y',
    })
  );

  // Health endpoint used by the container
  server.get('/health', (_req, res) => res.status(200).json({ status: 'UP' }));

  // All regular routes use the Universal engine
  server.get('*', (req, res) => {
    const protocol = req.protocol;
    const host = req.get('host');
    res.render(indexHtml, {
      req,
      providers: [
        { provide: APP_BASE_HREF, useValue: req.baseUrl },
        // The base URL enables the app to load the app config file during server-side rendering.
        {
          provide: 'APP_BASE_URL',
          // the format of ${host} is `host:port`
          useFactory: () => `${protocol}://${host}`,
          deps: [],
        },
        {
          provide: 'APP_PORT',
          useValue: PORT,
          deps: [],
        },
      ],
    });
  });

  return server;
}

function run(): void {
  // Start up the Node server
  const server = app();
  server.listen(PORT, () => {
    console.log(`Node Express server listening on http://localhost:${PORT}`);
  });
}

// Webpack will replace 'require' with '__webpack_require__'
// '__non_webpack_require__' is a proxy to Node 'require'
// The below code is to ensure that the server is run only when not requiring the bundle.
/* eslint-disable camelcase,no-undef */
declare const __non_webpack_require__: NodeRequire;
const mainModule = __non_webpack_require__.main;
const moduleFilename = (mainModule && mainModule.filename) || '';
if (moduleFilename === __filename || moduleFilename.includes('iisnode')) {
  run();
}

export default bootstrap;
