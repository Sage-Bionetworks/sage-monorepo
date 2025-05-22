import { ModelDetails } from '@sagebionetworks/model-ad/api-client-angular';
import { NextFunction, Request, Response } from 'express';
import { cache, setHeaders } from '../helpers';
import { ModelDetailsCollection } from '../models';

export async function getModelDetails(model: string) {
  const cacheKey = 'model-details-' + model;
  const cachedResult: ModelDetails | null | undefined = cache.get(cacheKey);

  // If we have a cached result (including null), return it
  if (cachedResult !== undefined) {
    return cachedResult;
  }

  const result = await ModelDetailsCollection.findOne({
    model: model,
  })
    .lean()
    .exec();

  cache.set(cacheKey, result);
  return result;
}

export async function modelDetailsRoute(req: Request, res: Response, next: NextFunction) {
  if (!req.params?.model) {
    res.status(400).contentType('application/problem+json').json({
      title: 'Bad Request',
      status: 400,
      detail: 'Model parameter is required',
    });
    return;
  }

  try {
    const result = await getModelDetails(req.params.model);

    if (!result) {
      res.status(404).contentType('application/problem+json').json({
        title: 'Not Found',
        status: 404,
        detail: 'Model not found',
      });
      return;
    }

    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}
