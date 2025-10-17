import { DiseaseCorrelation } from '@sagebionetworks/model-ad/api-client';
import { NextFunction, Request, Response } from 'express';
import { cache, setHeaders } from '../helpers';
import { DiseaseCorrelationCollection } from '../models';

enum KNOWN_CATEGORIES {
  CONSENSUS_NETWORK_MODULES = 'CONSENSUS NETWORK MODULES',
}

export async function getDiseaseCorrelations(cluster: string) {
  const cacheKey = 'diseaseCorrelations-' + cluster;
  const cachedResult: DiseaseCorrelation[] | null | undefined = cache.get(cacheKey);

  if (cachedResult !== undefined) {
    return cachedResult;
  }

  const result = await DiseaseCorrelationCollection.find({ cluster }).lean().exec();

  cache.set(cacheKey, result);
  return result;
}

export async function diseaseCorrelationRoute(req: Request, res: Response, next: NextFunction) {
  const categories = req.query.category;
  if (
    !categories ||
    !Array.isArray(categories) ||
    categories.length !== 2 ||
    !categories.every((f) => typeof f === 'string')
  ) {
    res
      .status(400)
      .contentType('application/problem+json')
      .json({
        title: 'Bad Request',
        status: 400,
        detail: `Query parameter category must repeat twice (e.g. ?category=${KNOWN_CATEGORIES.CONSENSUS_NETWORK_MODULES}&category=subcategory) and each value must be a string`,
        instance: req.path,
      });
    return;
  }

  const [category, subcategory] = categories;

  if (category !== KNOWN_CATEGORIES.CONSENSUS_NETWORK_MODULES) {
    res
      .status(400)
      .contentType('application/problem+json')
      .json({
        title: 'Bad Request',
        status: 400,
        detail: `Only ${KNOWN_CATEGORIES.CONSENSUS_NETWORK_MODULES} category is supported`,
        instance: req.path,
      });
    return;
  }

  try {
    const result = await getDiseaseCorrelations(subcategory);

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
