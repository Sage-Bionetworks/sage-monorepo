import { Model } from '@sagebionetworks/model-ad/api-client-angular';
import { NextFunction, Request, Response } from 'express';
import { cache, setHeaders } from '../helpers';
import { ModelCollection } from '../models';

export async function getModel(name: string) {
  const cacheKey = 'model-' + name;
  const cachedResult: Model | null | undefined = cache.get(cacheKey);

  // If we have a cached result (including null), return it
  if (cachedResult !== undefined) {
    return cachedResult;
  }

  const result = await ModelCollection.findOne({
    name: name,
  })
    .lean()
    .exec();

  cache.set(cacheKey, result);
  return result;
}

export async function modelRoute(req: Request, res: Response, next: NextFunction) {
  if (!req.params?.name) {
    res.status(400).contentType('application/problem+json').json({
      title: 'Bad Request',
      status: 400,
      detail: 'Name parameter is required',
      instance: req.path,
    });
    return;
  }

  try {
    const result = await getModel(req.params.name);

    if (!result) {
      res.status(404).contentType('application/problem+json').json({
        title: 'Not Found',
        status: 404,
        detail: 'Model not found',
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
