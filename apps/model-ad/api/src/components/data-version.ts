import { DataVersion } from '@sagebionetworks/model-ad/api-client';
import { NextFunction, Request, Response } from 'express';
import { buildCacheKey, cache, sendProblemJson, setHeaders } from '../helpers';
import { DataVersionCollection } from '../models';

export async function getDataVersion() {
  const cacheKey = buildCacheKey('dataVersion');
  const cachedResult: DataVersion | null | undefined = cache.get(cacheKey);

  if (cachedResult !== undefined) {
    return cachedResult;
  }

  const result = await DataVersionCollection.findOne();

  if (result !== null && result !== undefined) {
    cache.set(cacheKey, result);
  } else {
    // TODO add error handling here
  }

  return result;
}

export async function dataVersionRoute(req: Request, res: Response, next: NextFunction) {
  try {
    const result = await getDataVersion();

    if (!result) {
      sendProblemJson(res, 404, 'Not Found', 'Data Version not found', req.path);
      return;
    }

    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}
