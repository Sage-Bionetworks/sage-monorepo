import { Model } from '@sagebionetworks/model-ad/api-client';
import { NextFunction, Request, Response } from 'express';
import { buildCacheKey, cache, sendProblemJson, setHeaders } from '../helpers';
import { ModelCollection } from '../models';

export async function getModel(name: string) {
  const cacheKey = buildCacheKey('model', name);
  const cachedResult: Model | null | undefined = cache.get(cacheKey);

  // If we have a cached result (including null), return it
  if (cachedResult !== undefined) {
    return cachedResult;
  }
  const result = await ModelCollection.findOne({ name }).lean().exec();
  cache.set(cacheKey, result);
  return result;
}

export async function modelRoute(req: Request, res: Response, next: NextFunction) {
  if (!req.params?.name) {
    sendProblemJson(res, 400, 'Bad Request', 'Name parameter is required', req.path);
    return;
  }

  try {
    const result = await getModel(req.params.name);

    if (!result) {
      sendProblemJson(res, 404, 'Not Found', 'Model not found', req.path);
      return;
    }

    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}
