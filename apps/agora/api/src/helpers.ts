import { Response } from 'express';
import debug from 'debug';
import NodeCache from 'node-cache';

export function setHeaders(res: Response) {
  res.setHeader('Cache-Control', 'no-cache, no-store, must-revalidate');
  res.setHeader('Pragma', 'no-cache');
  res.setHeader('Expires', 0);
}

// Normalize a port into a number, string, or false.
export function normalizePort(val: any) {
  const tPort = parseInt(val, 10);

  if (isNaN(tPort)) {
    // named pipe
    return val;
  }

  if (tPort >= 0) {
    // port number
    return tPort;
  }

  return false;
}

// Event listener for HTTP server "error" event
export function onError(error: any, port: any) {
  if (error.syscall !== 'listen') {
    throw error;
  }

  const bind = typeof port === 'string' ? 'Pipe ' + port : 'Port ' + port;

  // handle specific listen errors with friendly messages
  switch (error.code) {
    case 'EACCES':
      console.error(bind + ' requires elevated privileges');
      process.exit(1);
      break;
    case 'EADDRINUSE':
      console.error(bind + ' is already in use');
      process.exit(1);
      break;
    default:
      throw error;
  }
}

// Event listener for HTTP server "listening" event
export function onListening(address: any) {
  debug('Listening on ' + typeof address === 'string' ? 'pipe ' + address : 'port ' + address.port);
}

// -------------------------------------------------------------------------- //
// Cache
// -------------------------------------------------------------------------- //
export const cache = new NodeCache();

// TODO: Performance issues with node-cache on large object, this should be revisited when possible.
// For now used AlternativeCache (local variables) to store large set of data.

class AlternativeCache {
  data: { [key: string]: any } = {};

  set(key: string, data: any) {
    this.data[key] = data;
  }

  get(key: string) {
    return this.data[key] || undefined;
  }
}

export const altCache = new AlternativeCache();
