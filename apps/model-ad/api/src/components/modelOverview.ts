import { NextFunction, Request, Response } from 'express';
import { cache, setHeaders } from '../helpers';
import { ModelOverviewCollection } from '../models';
import { ModelOverview } from '@sagebionetworks/model-ad/api-client-angular';

export async function getModelOverview(page: string) {
  const cacheKey = 'modelOverview-' + page;
  const cachedResult: ModelOverview[] | null | undefined = cache.get(cacheKey);

  // If we have a cached result (including null), return it
  if (cachedResult !== undefined) {
    return cachedResult;
  }

  const result = await ModelOverviewCollection.find({ page }).lean().exec();

  cache.set(cacheKey, result);
  return result;
}

export async function modelOverviewRoute(req: Request, res: Response, next: NextFunction) {
  try {
    const result = await getModelOverview(req.query.page as string);

    if (!result || result.length === 0) {
      res.status(404).contentType('application/problem+json').json({
        title: 'Not Found',
        status: 404,
        detail: 'Model overview data not found',
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
