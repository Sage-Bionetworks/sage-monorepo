import { NextFunction, Request, Response } from 'express';
import { cache, setHeaders } from '../helpers';
import { ComparisonToolConfigCollection } from '../models';
import { ComparisonToolConfig } from '@sagebionetworks/model-ad/api-client';

export async function getComparisonToolConfigs() {
  const cacheKey = 'comparisonToolConfig';
  const cachedResult: ComparisonToolConfig[] | null | undefined = cache.get(cacheKey);

  // If we have a cached result (including null), return it
  if (cachedResult !== undefined) {
    return cachedResult;
  }

  const result = await ComparisonToolConfigCollection.find().lean().exec();

  if (result !== null && result !== undefined) {
    cache.set(cacheKey, result);
  } else {
    // TODO add error handling here
  }

  return result;
}

export async function comparisonToolConfigRoute(req: Request, res: Response, next: NextFunction) {
  try {
    const result = await getComparisonToolConfigs();

    if (!result || result.length === 0) {
      res.status(404).contentType('application/problem+json').json({
        title: 'Not Found',
        status: 404,
        detail: 'Comparison Tool configs not found',
        instance: req.path,
      });
      return;
    }

    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}
