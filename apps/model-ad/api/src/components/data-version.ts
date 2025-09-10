import { DataVersion } from '@sagebionetworks/model-ad/api-client-angular';
import { NextFunction, Request, Response } from 'express';
import { cache, setHeaders } from '../helpers';
import { DataVersionCollection } from '../models';

export async function getDataVersion() {
  const cacheKey = 'dataVersion';
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
      res.status(404).contentType('application/problem+json').json({
        title: 'Not Found',
        status: 404,
        detail: 'Data Version not found',
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
