import { NextFunction, Request, Response } from 'express';
import { cache, setHeaders } from '../helpers';
import { ComparisonToolConfigCollection } from '../models';
import { ComparisonToolConfig } from '@sagebionetworks/model-ad/api-client';

export async function getComparisonToolConfig(page: string) {
  const cacheKey = 'comparisonToolConfig-' + page;
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
    res.status(400).contentType('application/problem+json').json({
      title: 'Bad Request',
      status: 400,
      detail: 'Page parameter is required',
      instance: req.path,
    });
    return;
  }

  try {
    const result = await getComparisonToolConfig(req.query.page as string);

    if (!result || result.length === 0) {
      res.status(404).contentType('application/problem+json').json({
        title: 'Not Found',
        status: 404,
        detail: 'Comparison Tool config not found',
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
