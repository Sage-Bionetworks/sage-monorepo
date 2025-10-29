import { ComparisonToolConfig } from '@sagebionetworks/model-ad/api-client';
import { NextFunction, Request, Response } from 'express';
import { buildCacheKey, cache, sendProblemJson, setHeaders } from '../helpers';
import { ComparisonToolConfigCollection } from '../models';

export async function getComparisonToolConfig(page: string) {
  const cacheKey = buildCacheKey('comparisonToolConfig', page);
  const cachedResult: ComparisonToolConfig[] | null | undefined = cache.get(cacheKey);

  // If we have a cached result (including null), return it
  if (cachedResult !== undefined) {
    return cachedResult;
  }

  const result = await ComparisonToolConfigCollection.find({ page }).lean().exec();

  cache.set(cacheKey, result);
  return result;
}

export async function comparisonToolConfigRoute(req: Request, res: Response, next: NextFunction) {
  if (!req.query.page) {
    sendProblemJson(res, 400, 'Bad Request', 'Page parameter is required', req.path);
    return;
  }

  try {
    const result = await getComparisonToolConfig(req.query.page as string);

    if (!result || result.length === 0) {
      sendProblemJson(res, 404, 'Not Found', 'Comparison Tool config not found', req.path);
      return;
    }

    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}
