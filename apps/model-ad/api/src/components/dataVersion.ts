import { NextFunction, Request, Response } from 'express';
import { cache, setHeaders } from '../helpers';
import { DataVersionCollection } from '../models';
import { Dataversion } from '@sagebionetworks/model-ad/api-client-angular';

export async function getDataVersion() {
  const cacheKey = 'dataVersion';
  const cachedResult: Dataversion | null | undefined = cache.get(cacheKey);

  if (cachedResult !== undefined) {
    return cachedResult;
  }

  try {
    const result = await DataVersionCollection.findOne();
    if (result !== null && result !== undefined) {
      cache.set(cacheKey, result);
    }
    return result;
  } catch (err) {
    // Log DB errors and return undefined so the route handler can handle it
    console.error('Error fetching data version:', err);
    return undefined;
  }
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
