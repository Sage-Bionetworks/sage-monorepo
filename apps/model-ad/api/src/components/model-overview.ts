import { NextFunction, Request, Response } from 'express';
import { cache, setHeaders } from '../helpers';
import { ModelOverviewCollection } from '../models';
import { ModelOverview } from '@sagebionetworks/model-ad/api-client-angular';

export async function getModelOverviews() {
  const cacheKey = 'modelOverview';
  const cachedResult: ModelOverview[] | null | undefined = cache.get(cacheKey);

  if (cachedResult !== undefined) {
    return cachedResult;
  }

  const result = await ModelOverviewCollection.find().lean().exec();

  cache.set(cacheKey, result);
  return result;
}

export async function modelOverviewRoute(req: Request, res: Response, next: NextFunction) {
  try {
    const result = await getModelOverviews();

    if (!result || result.length === 0) {
      res.status(404).contentType('application/problem+json').json({
        title: 'Not Found',
        status: 404,
        detail: 'Model Overview data not found',
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
