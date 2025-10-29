import { ItemFilterTypeQuery } from '@sagebionetworks/model-ad/api-client';
import { Response } from 'express';
import mongoose from 'mongoose';
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
// Query parameter validation helpers
// -------------------------------------------------------------------------- //
export function validateItemFilterType(
  itemFilterType: any,
  reqPath: string,
): { status: number; title: string; detail: string; instance: string } | undefined {
  if (
    itemFilterType &&
    itemFilterType !== ItemFilterTypeQuery.Include &&
    itemFilterType !== ItemFilterTypeQuery.Exclude
  ) {
    return {
      status: 400,
      title: 'Bad Request',
      detail: `Query parameter itemFilterType must be either '${ItemFilterTypeQuery.Include}' or '${ItemFilterTypeQuery.Exclude}' if provided`,
      instance: reqPath,
    };
  }
  return undefined;
}

export function validateItems(
  items: any,
  reqPath: string,
): { status: number; title: string; detail: string; instance: string } | undefined {
  if (items) {
    if (!Array.isArray(items) || !items.every((f) => typeof f === 'string')) {
      return {
        status: 400,
        title: 'Bad Request',
        detail: `Query parameter items must be a list of strings`,
        instance: reqPath,
      };
    }
  }
  return undefined;
}

// -------------------------------------------------------------------------- //
// Query builder helper
// -------------------------------------------------------------------------- //
export function buildIdQuery(items: string[], itemFilterType: ItemFilterTypeQuery) {
  const objectIds = items.map((id) => new mongoose.Types.ObjectId(id));
  if (itemFilterType === ItemFilterTypeQuery.Include) {
    return { $in: objectIds };
  } else {
    return { $nin: objectIds };
  }
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

// -------------------------------------------------------------------------- //
// Helpers
// -------------------------------------------------------------------------- //
export function normalizeToStringArray(param: string | string[] | undefined): string[] | undefined {
  if (param === undefined) {
    return undefined;
  }
  if (Array.isArray(param)) {
    return param;
  }
  return [param];
}
