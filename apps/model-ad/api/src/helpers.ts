import { Response } from 'express';
import NodeCache from 'node-cache';

export function setHeaders(res: Response) {
  res.setHeader('Cache-Control', 'no-cache, no-store, must-revalidate');
  res.setHeader('Pragma', 'no-cache');
  res.setHeader('Expires', 0);
  res.setHeader('Content-Type', 'application/json');
}

// -------------------------------------------------------------------------- //
// Problem+JSON error response helper
// -------------------------------------------------------------------------- //
export function sendProblemJson(
  res: Response,
  status: number,
  title: string,
  detail: string,
  instance: string,
) {
  res.status(status).contentType('application/problem+json').json({
    title,
    status,
    detail,
    instance,
  });
}

// -------------------------------------------------------------------------- //
// Cache key builder
// -------------------------------------------------------------------------- //
export function buildCacheKey(prefix: string, ...args: any[]) {
  return prefix + '-' + args.map((a) => JSON.stringify(a)).join('-');
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
    return this.data[key] ?? undefined;
  }
}

export const altCache = new AlternativeCache();
