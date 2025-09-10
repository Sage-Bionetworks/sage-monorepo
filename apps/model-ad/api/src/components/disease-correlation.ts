import { DiseaseCorrelation } from '@sagebionetworks/model-ad/api-client-angular';
import { NextFunction, Request, Response } from 'express';
import { cache, setHeaders } from '../helpers';
import { DiseaseCorrelationCollection } from '../models';

export async function getDiseaseCorrelations() {
  const cacheKey = 'diseaseCorrelations';
  const cachedResult: DiseaseCorrelation[] | null | undefined = cache.get(cacheKey);

  if (cachedResult !== undefined) {
    return cachedResult;
  }

  const result = await DiseaseCorrelationCollection.find().lean().exec();

  cache.set(cacheKey, result);
  return result;
}

export async function diseaseCorrelationRoute(req: Request, res: Response, next: NextFunction) {
  try {
    const result = await getDiseaseCorrelations();

    if (!result || result.length === 0) {
      res.status(404).contentType('application/problem+json').json({
        title: 'Not Found',
        status: 404,
        detail: 'Disease Correlation data not found',
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
